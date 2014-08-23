import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * TODO Put here a description of what this class does.
 *
 * @author xizhzhao.
 *         Created 2014-8-20.
 */
public class FoodMap {
	private Map<String, Map<String, Float>> fMap;
	private Map<String, FoodRange> frMap;
	private Map<String, Category> ctgMap;

	public FoodMap() {
		super();
		this.fMap = new HashMap<String, Map<String, Float>>();
		frMap = new HashMap<String, FoodRange>();
		ctgMap = new HashMap<String, Category>();
	}
	
	public Object[] getFoods(){
		return fMap.keySet().toArray();
	}
	
	public Object[] getCategories(){
		return ctgMap.keySet().toArray();
	}
	
	public void setFoodRangeMin(String food, float min){
		if(!frMap.containsKey(food)){
			frMap.put(food, new FoodRange(0, 0));
		}
		frMap.get(food).setMin(min);
	}
	
	public float getFoodRangeMin(String food){
		return frMap.get(food).getMin();
	}	
	
	public void setFoodRangeMax(String food, float max){
		if(!frMap.containsKey(food)){
			frMap.put(food, new FoodRange(0, 0));
		}
		frMap.get(food).setMax(max);
	}
	
	public float getFoodRangeMax(String food){
		return frMap.get(food).getMax();
	}	
	
	public void addFoodCategoryValue(String food, String category, float val){
		if(!fMap.containsKey(food)){
			Map t = new HashMap();
			t.put(category, Float.valueOf(val));
			this.fMap.put(food, t);
		}else{
			this.fMap.get(food).put(category, val);
		}
	}
	
	public float getFoodCategoryValue(String food, String category){
		return fMap.get(food).get(category).floatValue();
	}
	
	public void addCategory(String category, Category trs){
		ctgMap.put(category, trs);
	}
	
	public void setCategoryTarget(String category, float target){
		(ctgMap.get(category)).target = target;
	}
	
	public void setCategoryRange(String category, float range){
		(ctgMap.get(category)).range = range;
	}	
	
	public float getCategoryTarget(String category){
		return (float)(ctgMap.get(category)).target;
	}
	
	public float getCategoryRange(String category){
		return (float)(ctgMap.get(category)).range;
	}
		
}
