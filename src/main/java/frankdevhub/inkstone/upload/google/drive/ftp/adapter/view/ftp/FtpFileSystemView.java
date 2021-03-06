package nyoibo.inkstone.upload.google.drive.ftp.adapter.view.ftp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ftpserver.command.impl.MLSD;
import org.apache.ftpserver.command.impl.RETR;
import org.apache.ftpserver.command.impl.RNTO;
import org.apache.ftpserver.ftplet.FileSystemFactory;
import org.apache.ftpserver.ftplet.FileSystemView;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpFile;
import org.apache.ftpserver.ftplet.User;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import nyoibo.inkstone.upload.google.drive.ftp.adapter.controller.Controller;
import nyoibo.inkstone.upload.google.drive.ftp.adapter.model.Cache;
import nyoibo.inkstone.upload.google.drive.ftp.adapter.model.GFile;
import nyoibo.inkstone.upload.google.drive.ftp.adapter.service.FtpGdriveSynchService;
import nyoibo.inkstone.upload.google.drive.ftp.adapter.utils.OSUtils;

/**
 * <p>Title:FtpFileSystemView.java</p>  
 * <p>Description: </p>  
 * <p>Copyright: Copyright (c) 2019</p>  
 * <p>Company: www.frankdevhub.site</p>
 * <p>github: https://github.com/frankdevhub</p>  
 * @author frankdevhub   
 * @date:2019-04-23 17:00
 */

public class FtpFileSystemView implements FileSystemFactory, FileSystemView {

	static final String FILE_SEPARATOR = "/";
	private static final Log LOG = LogFactory.getLog(FtpFileSystemView.class);
	private static final String DUP_FILE_TOKEN = "__ID__";
	private static final String FILE_PARENT = "..";

	private static final String FILE_SELF = ".";

	private final Pattern ENCODED_FILE_PATTERN = Pattern
			.compile("^(.*)\\Q" + DUP_FILE_TOKEN + "\\E(.{28})\\Q" + DUP_FILE_TOKEN + "\\E(.*)$");

	private final User user;
	private final Controller controller;
	private final Cache model;
	private final Pattern illegalChars;
	private FtpFileWrapper home;
	private FtpFileWrapper currentDir;
	private FtpGdriveSynchService cacheUpdater;

	public FtpFileSystemView(Controller controller, Cache model, Pattern illegalChars, User user,
			FtpGdriveSynchService cacheUpdater) {
		this.controller = controller;
		this.model = model;
		this.illegalChars = illegalChars;
		this.user = user;
		this.cacheUpdater = cacheUpdater;
	}

	@Override
	public FileSystemView createFileSystemView(User user) {
		LOG.info("Creating ftp view for user '" + user + "'...");
		return new FtpFileSystemView(controller, model, illegalChars, user, cacheUpdater);
	}

	@Override
	public boolean isRandomAccessible() {
		return true;
	}

	@Override
	public FtpFile getHomeDirectory() {
		LOG.debug("Getting home directory for user '" + user + "'...");
		return home;
	}

	@Override
	public FtpFile getWorkingDirectory() {

		initWorkingDirectory();

		return currentDir;
	}

	private void initWorkingDirectory() {
		if (currentDir == null) {
			synchronized (this) {
				if (currentDir == null) {
					LOG.info("Initializing ftp view...");
					this.home = user.getHomeDirectory().equals("")
							? new FtpFileWrapper(this, controller, null, model.getFile("root"), "/")
							: getFileByRelativePath(
									new FtpFileWrapper(this, controller, null, model.getFile("root"), "/"),
									user.getHomeDirectory());
					this.currentDir = this.home;
				}
			}
		}
	}

	@Override
	public boolean changeWorkingDirectory(String path) throws FtpException {
		try {
			initWorkingDirectory();

			if (path.length() > 1 && path.endsWith(FILE_SEPARATOR))
				path = path.substring(0, path.length() - 1);

			LOG.debug("Changing working directory from '" + currentDir + "' to '" + path + "'...");

			if (FILE_SEPARATOR.equals(path)) {
				currentDir = home;
				return true;
			}

			if (FILE_SELF.equals(path)) {
				return true;
			}

			if (FILE_PARENT.equals(path)) {
				FtpFileWrapper parentFile = currentDir.getParentFile();
				if (parentFile == null) {
					return true;
				}

				if (currentDir.getAbsolutePath().equals(home.getAbsolutePath())) {
					return false;
				}

				currentDir = currentDir.getParentFile();
				return true;
			}

			if (home.getAbsolutePath().equals(path)) {
				currentDir = home;
				return true;
			} else if (home.getAbsolutePath().startsWith(path)) {
				return false;
			}

			FtpFileWrapper file;
			if (path.startsWith(FILE_SEPARATOR)) {
				LOG.debug("Changing working directory to absolute path '" + path + "'...");
				file = getFileByAbsolutePath(path);
			} else {
				LOG.debug("Changing working directory to relative path '" + path + "'...");
				file = getFileByRelativePath(currentDir, path);
			}

			if (file != null && file.isDirectory()) {
				currentDir = file;
				return true;
			}

			LOG.warn("File doesn't exist or is not a directory: '" + file + "'...");
			return false;
		} catch (Exception e) {
			throw new FtpException(e.getMessage(), e);
		}
	}

	@Override
	public void dispose() {
		LOG.info("Disposing ftp view...");
		currentDir = null;
		LOG.info("Stopping cache updated...");
		cacheUpdater.stop();
	}

	@Override
	public FtpFile getFile(String fileName) throws FtpException {

		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		StackTraceElement caller = stackTraceElements[2];
		if (RNTO.class.getName().equals(caller.getClassName()) && fileName.contains(DUP_FILE_TOKEN)) {
			LOG.info(
					"User is renaming a file which contains special chars to this gdrive ftp adapter. Please avoid using the token '"
							+ DUP_FILE_TOKEN + "' in the filename.");
		}

		LOG.debug("Getting file '" + fileName + "'...");

		initWorkingDirectory();

		try {
			if ("./".equals(fileName)) {
				return currentDir;
			}

			if (fileName.length() == 0) {
				return currentDir;
			}

			return fileName.startsWith(FILE_SEPARATOR) ? getFileByAbsolutePath(fileName)
					: getFileByName(currentDir, fileName);
		} catch (IllegalArgumentException e) {
			LOG.error(e.getMessage());
			throw new FtpException(e.getMessage(), e);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			throw new FtpException(e.getMessage(), e);
		}
	}

	private FtpFileWrapper getFileByAbsolutePath(String path) {
		if (!path.startsWith(FILE_SEPARATOR)) {
			throw new IllegalArgumentException("Path '" + path + "' should start with '" + FILE_SEPARATOR + "'");
		}
		if (currentDir.getAbsolutePath().equals(path)) {
			LOG.debug("Requested file is the current dir '" + currentDir + "'");
			return currentDir;
		}

		FtpFileWrapper folder;
		if (path.startsWith(
				currentDir.isRoot() ? currentDir.getAbsolutePath() : currentDir.getAbsolutePath() + FILE_SEPARATOR)) {
			folder = currentDir;
			if (folder.isRoot()) {
				path = path.substring(1);
			} else {
				path = path.substring(folder.getAbsolutePath().length() + 1);
			}
		} else {
			folder = home;
			if (path.startsWith(folder.getAbsolutePath())) {
				path = path.substring(folder.getAbsolutePath().length());
			} else {
				path = path.substring(1);
			}
		}

		return getFileByRelativePath(folder, path);
	}

	private FtpFileWrapper getFileByRelativePath(FtpFileWrapper folder, String path) {
		FtpFileWrapper file = null;
		if (!path.contains(FILE_SEPARATOR)) {

			LOG.debug("Getting file '" + path + "' for directory '" + folder.getAbsolutePath() + "'...");
			file = getFileByName(folder, path);
			return file;
		}

		LOG.debug("Getting file '" + path + "' inside directory '" + folder.getAbsolutePath() + "'...");
		for (String part : path.split(FtpFileSystemView.FILE_SEPARATOR)) {
			file = getFileByName(folder, part);
			folder = file;
		}
		return file;
	}

	private FtpFileWrapper getFileByName(FtpFileWrapper folder, String fileName) {
		String absolutePath = folder.getAbsolutePath() + (folder.isRoot() ? "" : FILE_SEPARATOR) + fileName;
		LOG.debug("Querying for file '" + absolutePath + "' inside folder '" + folder + "'...");

		try {
			GFile fileByName = model.getFileByName(folder.getId(), fileName);
			if (fileByName != null) {
				LOG.debug("File '" + fileName + "' found");
				return createFtpFileWrapper(folder, fileByName, fileName);
			}
			LOG.debug("File '" + fileName + "' doesn't exist!");

			int nextIdx = fileName.indexOf(DUP_FILE_TOKEN);
			if (nextIdx != -1 && ENCODED_FILE_PATTERN.matcher(fileName).matches()) {

				Matcher matcher = ENCODED_FILE_PATTERN.matcher(fileName);

				matcher.find();

				String expectedFileName = matcher.group(1) + matcher.group(3);
				final String fileId = matcher.group(2);

				LOG.debug("Searching encoded file '" + folder.getAbsolutePath()
						+ (folder.isRoot() ? "" : FILE_SEPARATOR) + expectedFileName + "' ('" + fileId + "')...");
				GFile gfile = model.getFile(fileId);
				if (gfile != null && (expectedFileName.equals(gfile.getName())
						|| removeIllegalChars(gfile.getName()).equals(expectedFileName))) {

					return createFtpFileWrapper(folder, gfile, fileName);
				}

				LOG.info("Encoded file '" + folder.getAbsolutePath() + (folder.isRoot() ? "" : FILE_SEPARATOR)
						+ expectedFileName + "' ('" + fileId + "') not found");
			}

			return createFtpFileWrapper(folder, new GFile(Collections.singleton(folder.getId()), fileName), fileName);
		} catch (IncorrectResultSizeDataAccessException e) {

			return createFtpFileWrapper(folder, new GFile(Collections.singleton(folder.getId()), fileName), fileName);
		}
	}

	private FtpFileWrapper createFtpFileWrapper(FtpFileWrapper folder, GFile gFile, String filename) {

		String filenameWithoutIllegalChars = removeIllegalChars(filename);
		if (!filename.equals(filenameWithoutIllegalChars)) {
			filename = encodeFilename(filenameWithoutIllegalChars, gFile.getId());
			LOG.info("Filename with illegal chars '" + filename + "' has been given virtual name '"
					+ filenameWithoutIllegalChars + "'");
		}

		String absolutePath = folder == null ? filename
				: folder.isRoot() ? FILE_SEPARATOR + filename : folder.getAbsolutePath() + FILE_SEPARATOR + filename;
		LOG.debug("Creating file wrapper " + absolutePath);
		return new FtpFileWrapper(this, controller, folder, gFile, filename);
	}

	private String removeIllegalChars(String filename) {
		return illegalChars.matcher(filename).replaceAll("_");
	}

	List<FtpFile> listFiles(FtpFileWrapper folder) {

		LOG.debug("Listing " + folder.getAbsolutePath());

		List<GFile> query = controller.getFiles(folder.getId());
		if (query.isEmpty()) {
			return Collections.emptyList();
		}

		Map<String, FtpFileWrapper> allFilenames = new HashMap<>(query.size());

		List<FtpFileWrapper> ret = new ArrayList<>(query.size());

		for (GFile ftpFile : query) {

			FtpFileWrapper fileWrapper = createFtpFileWrapper(folder, ftpFile, ftpFile.getName());
			ret.add(fileWrapper);

			String filename = fileWrapper.getName();
			String uniqueFilename = OSUtils.isWindows() ? filename.toLowerCase()
					: OSUtils.isUnix() ? filename : filename;

			if (!allFilenames.containsKey(uniqueFilename)) {
				allFilenames.put(uniqueFilename, fileWrapper);
				continue;
			}

			final FtpFileWrapper firstFileDuplicated = allFilenames.get(uniqueFilename);
			firstFileDuplicated.setVirtualName(encodeFilename(filename, firstFileDuplicated.getId()));
			fileWrapper.setVirtualName(encodeFilename(filename, ftpFile.getId()));

			LOG.debug("Generated virtual filename for duplicated file '" + firstFileDuplicated.getName() + "'");
			LOG.debug("Generated virtual filename for duplicated file '" + fileWrapper.getName() + "'");
		}

		return new ArrayList<>(ret);
	}

	private String encodeFilename(String filename, String fileId) {

		final int fileSuffixPos = filename.lastIndexOf('.');
		String ext = "";
		if (fileSuffixPos != -1) {
			ext = filename.substring(fileSuffixPos);
			filename = filename.substring(0, fileSuffixPos);
		}

		return filename + DUP_FILE_TOKEN + fileId + DUP_FILE_TOKEN + ext;
	}
}
