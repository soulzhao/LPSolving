import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * TODO Put here a description of what this class does.
 *
 * @author xizhzhao.
 *         Created 2014-8-20.
 */
public class CSVFileHanlder {
	
	private String filename;
	private BufferedReader fr;
	private FoodMap fMap;
	
	
	public CSVFileHanlder(String filename) {
		super();
		this.filename = filename;
		fMap = new FoodMap();
	}

	private boolean openFile(){
		try {
			fr = new BufferedReader(new FileReader(this.filename));
			return true;
		} catch (FileNotFoundException exception) {
			exception.printStackTrace();
			return false;
		}
	}
	
	public void readFile(){
		if(this.openFile()){
			try {
				int linenum = 0;
				String line = "";
				
				List<String> titles = new ArrayList<String>();
				
				while((line = fr.readLine())!=null){
					linenum++;
					String [] vals = line.split(",");
					if(linenum == 1) {
						// set titles
						for(int i = 1; i < vals.length; i++){
							fMap.addCategory(vals[i], new Category(0, 0));
						}
						titles = Arrays.asList(vals);
						
					}else if(line.startsWith("Target") || 
							line.startsWith("target")){
						// set category target
						for(int i = 1; i < vals.length; i++){
							fMap.setCategoryTarget(titles.get(i), Float.valueOf(vals[i]).floatValue());
						}
					}else if(line.startsWith("Range") ||
							line.startsWith("range")){
						// set category range
						for(int i = 1; i < vals.length; i++){
							fMap.setCategoryRange(titles.get(i), Float.valueOf(vals[i]).floatValue());
						}			
					}else{
						// set food category value
						for(int i = 1; i < vals.length; i++){
							fMap.addFoodCategoryValue(vals[1], titles.get(i), Float.valueOf(vals[i]).floatValue());
						}
					}
				}
			} catch (IOException exception) {
				exception.printStackTrace();
			}
		}else{
			System.out.println("File open error!");
		}
	}

	public static void main(String[] args) {
		
	}

}
