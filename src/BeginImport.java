import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

import org.apache.commons.io.FileUtils;

public class BeginImport {

	public static void main(String[] args) {
		String[] command = {"cmd.exe", "/C", Constants.MONITOR};
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
		
		//transfer Files
		System.out.println("Done importing, transfering files.");
		File newMusic = new File(Constants.ADDFROM);
		String [] toAdd = newMusic.list();
		JFrame progress = new JFrame("Importing...");
		JProgressBar percent = new JProgressBar();
		progress.add(new JLabel("Importing to NoLa15"));
		progress.add(percent);
		progress.setAlwaysOnTop(true);
		progress.pack();
		progress.setVisible(true);
		double full = getFile(0, Constants.ADDFROM);
		double position = 0;
		percent.setMinimum(0);
		percent.setMaximum(100);
		percent.setStringPainted(true);
		for(String willAdd: toAdd){
			try {
				//Remove Directory if there
				FileUtils.deleteDirectory(new File(Constants.AUTOADD + willAdd));
				//Transfer
				FileUtils.moveDirectoryToDirectory(new File(Constants.ADDFROM + willAdd), new File(Constants.AUTOADD), false);
			} catch (IOException e) {
				e.printStackTrace();
			}
			position++;
			percent.setValue((int)((position/full)*100));
		}
		
		System.out.println("Done transfering.");
		progress.dispose();
	}

	private static int getFile(int count, String dirPath) 
	{
	    File f = new File(dirPath);
	    File[] files  = f.listFiles();

	    if(files != null)
	    for(int i=0; i < files.length; i++)
	    {
	        count ++;
	        File file = files[i];
	        if(file.isDirectory())
	        {   
	             getFile(count, file.getAbsolutePath()); 
	        }
	     }
	    return count;
	}
}
