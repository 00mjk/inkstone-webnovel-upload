package nyoibo.inkstone.upload.gui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;



/**
 * <p>Title:InkstoneUploadConsole.java</p>  
 * <p>Description: </p>  
 * <p>Copyright: Copyright (c) 2019</p>  
 * <p>Company: www.frankdevhub.site</p>
 * <p>github: https://github.com/frankdevhub</p>  
 * @author frankdevhub   
 * @date:2019-05-19 18:06
 */

public class InkstoneUploadConsole extends Dialog{
	private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());
	private Text bookListText;
	private Text chapterListText;
	private Text consoleTextArea;
	private Text webLinkText;
	private Text progressText;
	private Text weblinkUrl;

	public InkstoneUploadConsole(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);

		GridLayout gridLayout = (GridLayout) container.getLayout();
		gridLayout.numColumns = 4;

		Button bookListButton = formToolkit.createButton(container, "Config Book List", SWT.NONE);
		GridData gd_bookListButton = new GridData(SWT.LEFT, SWT.CENTER, false, false, 4, 1);
		gd_bookListButton.widthHint = 425;
		bookListButton.setLayoutData(gd_bookListButton);
		bookListButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});

		bookListText = formToolkit.createText(container, "bookListText", SWT.NONE);
		bookListText.setEditable(false);
		bookListText.setText("");
		GridData gd_bookListText = new GridData(SWT.LEFT, SWT.CENTER, true, false, 4, 1);
		gd_bookListText.widthHint = 422;
		bookListText.setLayoutData(gd_bookListText);

		Button chapterListButton = formToolkit.createButton(container, "Config Chapters ", SWT.NONE);
		GridData gd_chapterListButton = new GridData(SWT.LEFT, SWT.CENTER, false, false, 4, 1);
		gd_chapterListButton.widthHint = 423;
		chapterListButton.setLayoutData(gd_chapterListButton);
		chapterListButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});

		chapterListText = formToolkit.createText(container, "chapterListText", SWT.NONE);
		chapterListText.setEditable(false);
		chapterListText.setText("");
		chapterListText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));

		webLinkText = formToolkit.createText(container, "webLinkText", SWT.NONE);
		webLinkText.setEnabled(false);
		webLinkText.setToolTipText("");
		webLinkText.setEditable(false);
		webLinkText.setText("Web Link ");
		webLinkText.setBackground(container.getBackground());
		webLinkText.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 4, 1));

		weblinkUrl = new Text(container, SWT.BORDER);
		weblinkUrl.setText("https://inkstone.webnovel.com/book");
		weblinkUrl.setEditable(false);
		GridData gd_weblinkUrl = new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1);
		gd_weblinkUrl.widthHint = 186;
		weblinkUrl.setLayoutData(gd_weblinkUrl);
		formToolkit.adapt(weblinkUrl, true, true);

		progressText = formToolkit.createText(container, "progressText", SWT.NONE);
		progressText.setEnabled(false);
		progressText.setEditable(false);
		progressText.setText("Progress ");
		progressText.setBackground(container.getBackground());
		progressText.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 4, 1));

		ProgressBar progressBar = new ProgressBar(container, SWT.NONE);
		progressBar.setBackground(SWTResourceManager.getColor(SWT.COLOR_CYAN));
		GridData gdProgressBar = new GridData(SWT.LEFT, SWT.CENTER, true, false, 4, 1);
		gdProgressBar.widthHint = 425;
		progressBar.setLayoutData(gdProgressBar);
		formToolkit.adapt(progressBar, true, true);

		ScrolledComposite scrolledComposite = new ScrolledComposite(container,
				SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, false, false, 4, 1);
		gridData.widthHint = 367;
		gridData.heightHint = 218;
		scrolledComposite.setLayoutData(gridData);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		scrolledComposite.setAlwaysShowScrollBars(true);

		consoleTextArea = formToolkit.createText(scrolledComposite, "InkstoneConsoleTextArea", SWT.NONE);
		consoleTextArea.setEditable(false);
		consoleTextArea.setText("Waiting ......");
		consoleTextArea.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_FOREGROUND));
		consoleTextArea.setForeground(SWTResourceManager.getColor(SWT.COLOR_YELLOW));

		scrolledComposite.setContent(consoleTextArea);
		scrolledComposite.setMinSize(consoleTextArea.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		return container;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	@Override
	protected Point getInitialSize() {
		return new Point(450, 546);
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Select your novel");
		newShell.setImage(new Image(null, "src/main/resources/gui/favicon.ico"));
	}
	
	
	public static void main(String[] args) {
		Shell shell = Display.getDefault().getActiveShell();
		InkstoneUploadConsole console = new InkstoneUploadConsole(shell);
		console.open();
	}
}
