import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

public class ImportWithProgress {
	public static JProgressBar percent;

	@SuppressWarnings("serial")
	public static void main(String[] args) throws IOException {
		String[] command = { "cmd.exe", "/C", Constants.MONITOR };
		Process monitImport = null;
		ProcessBuilder monitor = new ProcessBuilder(command);

		try {
			monitImport = monitor.start();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			monitImport.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// transfer Files
		System.out.println("Done importing, transfering files.");
		JFrame progress = new JFrame("Importing...");
		progress.setLayout(new FlowLayout());
		percent = new JProgressBar();
		progress.add(new JButton(new AbstractAction("Cancel") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		}));
		progress.add(new JLabel("Importing to NoLa15: "));
		progress.add(percent);
		progress.setAlwaysOnTop(true);
		progress.pack();
		progress.setVisible(true);
		double full = getFile(0, Constants.ADDFROM);
		System.out.println("Files to transfer: " + full);
		double position = 0;
		percent.setMinimum(0);
		percent.setMaximum(100);
		percent.setStringPainted(true);

		// Transfer
		percent.setValue(0);
		getAndMoveFiles(full, position, Constants.ADDFROM, Constants.AUTOADD);

		System.out.println("Done transfering.");
		progress.dispose();
		Desktop.getDesktop()
				.open(new File(
						"C:\\ProgramData\\Microsoft\\Windows\\Start Menu\\Programs\\iTunes\\iTunes.lnk"));
		System.exit(0);
	}

	private static int getFile(int count, String dirPath) {
		File f = new File(dirPath);
		File[] files = f.listFiles();

		if (!files.equals(null)) {
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					count = getFile(count, files[i].getAbsolutePath());
				} else {
					count++;
				}
			}
		} else {
			count++;
		}

		return count;
	}

	private static double getAndMoveFiles(double total, double count,
			String fromPath, String toPath) {
		File f = new File(fromPath);
		File[] files = f.listFiles();

		if (files != null) {
			// Is a Directory
			for (int i = 0; i < files.length; i++) {
				File file = files[i];
				if (file.isDirectory()) {
					File newFile = new File(toPath + "\\" + file.getName());
					newFile.mkdir();
					count = getAndMoveFiles(total, count,
							file.getAbsolutePath(),
							toPath + "//" + file.getName());
					file.delete();
				} else {
					try {
						Files.move(Paths.get(fromPath + "\\" + file.getName()),
								Paths.get(toPath + "\\" + file.getName()),
								StandardCopyOption.REPLACE_EXISTING);
					} catch (IOException e) {
						e.printStackTrace();
					}

					count++;
					percent.setValue((int) ((count / total) * 100));
				}
			}
		}
		return count;
	}
}
