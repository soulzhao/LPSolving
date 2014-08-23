import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
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
	public FoodMap fMap;
	
	
	public CSVFileHanlder(String filename) {
		super();
		this.filename = filename;
		fMap = new FoodMap();
	}

	private boolean openFile(){
		try {
			fr = new BufferedReader(new InputStreamReader(new FileInputStream(new File(this.filename)), "UTF-8"));
			return true;
		} catch (FileNotFoundException exception) {
			exception.printStackTrace();
			return false;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean readFile(){
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
						for(int i = 1; i < (vals.length - 2); i++){
							fMap.addCategory(vals[i], new Category(0, 0));
							titles.add(vals[i]);
						}
					}else if(line.startsWith("Target") || 
							line.startsWith("target")){
						// set category target
						// we don't need to minus 2, because the last 2 rows do not have max and min
						for(int i = 1; i < vals.length; i++){
							fMap.setCategoryTarget(titles.get(i - 1), Float.valueOf(vals[i]).floatValue());
						}
					}else if(line.startsWith("Range") ||
							line.startsWith("range")){
						// set category range
						// we don't need to minus 2, because the last 2 rows do not have max and min
						for(int i = 1; i < vals.length; i++){
							fMap.setCategoryRange(titles.get(i - 1), Float.valueOf(vals[i]).floatValue());
						}			
					}else{
						// set food category value
						for(int i = 1; i < (vals.length - 2); i++){
							fMap.addFoodCategoryValue(vals[0], titles.get(i - 1), Float.valueOf(vals[i]).floatValue());
						}
						fMap.setFoodRangeMin(vals[0], Float.valueOf(vals[vals.length - 2]).floatValue());
						fMap.setFoodRangeMax(vals[0], Float.valueOf(vals[vals.length - 1]).floatValue());
					}
				}
				return true;
			} catch (IOException exception) {
				exception.printStackTrace();
				return false;
			}
		}else{
			System.out.println("File open error!");
			return false;
		}
	}
	
	public void closeFile() throws IOException{
		this.fr.close();
	}

	public static void main(String[] args) throws IOException {
		CSVFileHanlder csvfHanlder = new CSVFileHanlder("D:\\2.csv");
		if(csvfHanlder.readFile()){
			for(Object ctg : csvfHanlder.fMap.getCategories()){
				System.out.println(ctg + " Target: " + csvfHanlder.fMap.getCategoryTarget(ctg.toString()) + " Range: " + csvfHanlder.fMap.getCategoryRange(ctg.toString()));
			}
			
			for(Object food : csvfHanlder.fMap.getFoods()){
				System.out.println("\n ==== " + food.toString() + " ==== \n");
				System.out.println(" Min: " + csvfHanlder.fMap.getFoodRangeMin(food.toString()) + " Max: " + csvfHanlder.fMap.getFoodRangeMax(food.toString()));
				for(Object ctg : csvfHanlder.fMap.getCategories()){
					System.out.println(ctg + " : " + csvfHanlder.fMap.getFoodCategoryValue(food.toString(), ctg.toString()));
				}
			}
		}
		csvfHanlder.closeFile();
	}

}
