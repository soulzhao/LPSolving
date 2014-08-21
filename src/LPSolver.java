import java.io.IOException;

import org.gnu.glpk.GLPK;
import org.gnu.glpk.GLPKConstants;
import org.gnu.glpk.GlpkException;
import org.gnu.glpk.SWIGTYPE_p_double;
import org.gnu.glpk.SWIGTYPE_p_int;
import org.gnu.glpk.glp_prob;
import org.gnu.glpk.glp_smcp;


public class LPSolver {
	
	public static void main(String[] args) throws IOException {
		CSVFileHanlder csvfHanlder = new CSVFileHanlder("2.csv");
		
		if(csvfHanlder.readFile()){
	        glp_prob lp;
	        glp_smcp parm;
	        SWIGTYPE_p_int ind;
	        SWIGTYPE_p_double val;

	        int ret;

	        try {
	            // Create problem
	            lp = GLPK.glp_create_prob();
	            System.out.println("Problem created");
	            GLPK.glp_set_prob_name(lp, "food Problem");
	            
	            String [] flst = csvfHanlder.fMap.getFoods();
	            // Define columns -- number of food£¬ £¬ + 1  for the max virtual x0
	            GLPK.glp_add_cols(lp, flst.length + 1);
	            for(int i = 1; i <= flst.length; i++){
	            	 GLPK.glp_set_col_name(lp, i, flst[i - 1]);
	            	 GLPK.glp_set_col_kind(lp, i, GLPKConstants.GLP_CV);
	            	 GLPK.glp_set_col_bnds(lp, i, GLPKConstants.GLP_DB, 
	            			 csvfHanlder.fMap.getFoodRangeMin(flst[i - 1]), 
	            			 csvfHanlder.fMap.getFoodRangeMax(flst[i - 1])); // Double bounded
	            }
	            
	            // the virtual X0
	           	GLPK.glp_set_col_name(lp, flst.length + 1, "X0");
	           	GLPK.glp_set_col_kind(lp, flst.length + 1, GLPKConstants.GLP_CV);
	           	GLPK.glp_set_col_bnds(lp, flst.length + 1, GLPKConstants.GLP_LO, 0, 0);	            
	            

	            // Create constraints
	            
	            // Allocate memory
	            ind = GLPK.new_intArray(flst.length + 1);
	            val = GLPK.new_doubleArray(flst.length + 1);

	            String [] clst = csvfHanlder.fMap.getCategories();
	            // Create rows -- the number of categories
	            GLPK.glp_add_rows(lp, clst.length * 2);
	            
	            // this is for the sum of x1 ... xn
	            for(int i = 1; i <= clst.length; i++){
		            // Set row details
	            	String cName = clst[i - 1];
		            GLPK.glp_set_row_name(lp, i, cName);
		            float target = csvfHanlder.fMap.getCategoryTarget(cName);
		            float range = csvfHanlder.fMap.getCategoryRange(cName);
		            GLPK.glp_set_row_bnds(lp, i, GLPKConstants.GLP_DB, (1 - range) * target, target); //  (1 - range)*T <= p <= T
		            
		            for(int j = 1; j <= flst.length; j++){
			            GLPK.intArray_setitem(ind, j, j);
			            GLPK.doubleArray_setitem(val, j, csvfHanlder.fMap.getFoodCategoryValue(flst[j - 1], cName));
		            }
		         
		            GLPK.glp_set_mat_row(lp, i, flst.length, ind, val); // set the i row of the matrix, every time I set flst.length numbers
	            }
	            
	            // this is for the sum of x1 ... xn plus X0
	            for(int i = 1; i <= clst.length; i++){
		            // Set row details
	            	String cName = clst[i - 1];
		            GLPK.glp_set_row_name(lp, i, "Target" + cName);
		            float target = csvfHanlder.fMap.getCategoryTarget(cName);
		            GLPK.glp_set_row_bnds(lp, i, GLPKConstants.GLP_LO, target, 0); //  T <= u
		            
		            for(int j = 1; j <= flst.length; j++){
			            GLPK.intArray_setitem(ind, j, j);
			            GLPK.doubleArray_setitem(val, j, csvfHanlder.fMap.getFoodCategoryValue(flst[j - 1], cName));
		            }
		            GLPK.intArray_setitem(ind, flst.length + 1, flst.length + 1);
		            GLPK.doubleArray_setitem(val, flst.length + 1, 1);
		         
		            GLPK.glp_set_mat_row(lp, i, flst.length + 1, ind, val); // set the i row of the matrix, every time I set flst.length numbers
	            }

	            // Free memory
	            GLPK.delete_intArray(ind);
	            GLPK.delete_doubleArray(val);

	            // Define objective
	            // for the objective definition: refer http://www.doc88.com/p-09232355056.html for conversion from min{max[abs1, abs2 ... absn]}
	            GLPK.glp_set_obj_name(lp, "z");
	            GLPK.glp_set_obj_dir(lp, GLPKConstants.GLP_MIN);
	            //GLPK.glp_set_obj_coef(lp, 0, 1.); // constant set at pos 0
	            for(int i = 1; i < flst.length; i++){
	            	GLPK.glp_set_obj_coef(lp, i, 0);
	            }
	            GLPK.glp_set_obj_coef(lp, flst.length + 1, 1);

	            // Solve model
	            parm = new glp_smcp();
	            GLPK.glp_init_smcp(parm);
	            ret = GLPK.glp_simplex(lp, parm);

	            // Retrieve solution
	            if (ret == 0) {
	            	write_lp_solution(lp);
	            } else {
	                System.out.println("The problem could not be solved");
	            }

	            // Free memory
	            GLPK.glp_delete_prob(lp);
	        } catch (GlpkException ex) {
	            ex.printStackTrace();
	        }		
	        
		}
		csvfHanlder.closeFile();
	}
	
    /**
     * write simplex solution
     * @param lp problem
     */
    static void write_lp_solution(glp_prob lp) {
        int i;
        int n;
        String name;
        double val;

        name = GLPK.glp_get_obj_name(lp);
        val = GLPK.glp_get_obj_val(lp);
        System.out.print(name);
        System.out.print(" = ");
        System.out.println(val);
        n = GLPK.glp_get_num_cols(lp);
        for (i = 1; i <= n; i++) {
            name = GLPK.glp_get_col_name(lp, i);
            val = GLPK.glp_get_col_prim(lp, i);
            System.out.print(name);
            System.out.print(" = ");
            System.out.println(val);
        }
    }	

}
