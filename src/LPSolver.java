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
		CSVFileHanlder csvfHanlder = new CSVFileHanlder("D:\\2.csv");
		
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
	            
	            Object [] flst = csvfHanlder.fMap.getFoods();
	            // Define columns -- number of food£¬ £¬ + 1  for the max virtual x0
	            GLPK.glp_add_cols(lp, flst.length + 1);
	            
	            // the virtual X0
	           	GLPK.glp_set_col_name(lp, 1, "X0");
	           	GLPK.glp_set_col_kind(lp, 1, GLPKConstants.GLP_CV);
	           	GLPK.glp_set_col_bnds(lp, 1, GLPKConstants.GLP_LO, 0, 0);	     
	            
	            for(int i = 2; i <= (flst.length + 1); i++){
	            	 GLPK.glp_set_col_name(lp, i, flst[i - 2].toString());
	            	 GLPK.glp_set_col_kind(lp, i, GLPKConstants.GLP_CV);
	            	 GLPK.glp_set_col_bnds(lp, i, GLPKConstants.GLP_DB, 
	            			 csvfHanlder.fMap.getFoodRangeMin(flst[i - 2].toString()), 
	            			 csvfHanlder.fMap.getFoodRangeMax(flst[i - 2].toString())); // Double bounded
	            }

	            // Create constraints
	            
	            // Allocate memory
	            ind = GLPK.new_intArray(flst.length + 1);
	            val = GLPK.new_doubleArray(flst.length + 1);
	            parm = new glp_smcp();
	            
	            Object [] clst = csvfHanlder.fMap.getCategories();
	            // Create rows -- the number of categories * 2
	            GLPK.glp_add_rows(lp, clst.length * 2);
	            
	            // this is for the sum of x1 ... xn
	            for(int i = 1; i <= clst.length; i++){
		            // Set row details
	            	String cName = clst[i - 1].toString();
		            GLPK.glp_set_row_name(lp, i, cName);
		            float target = csvfHanlder.fMap.getCategoryTarget(cName);
		            float range = csvfHanlder.fMap.getCategoryRange(cName);
		            GLPK.glp_set_row_bnds(lp, i, GLPKConstants.GLP_DB, (1 - range) * target, target); //  (1 - range)*T <= p <= T
		            
		            GLPK.intArray_setitem(ind, 1, 1);
		            GLPK.doubleArray_setitem(val, 1, 0);
		            
		            for(int j = 2; j <= (flst.length + 1); j++){
			            GLPK.intArray_setitem(ind, j, j);
			            GLPK.doubleArray_setitem(val, j, csvfHanlder.fMap.getFoodCategoryValue(flst[j - 2].toString(), cName) / 100);
		            }
		            
//		            for(int j = 1;  j <= (flst.length + 1); j++){
//		            	System.out.println("pos " + GLPK.intArray_getitem(ind, j) + ": val " + GLPK.doubleArray_getitem(val, j));
//		            }
		            
		            GLPK.glp_set_mat_row(lp, i, flst.length + 1, ind, val); // set the i row of the matrix, every time I set (flst.length + 1) numbers
	            }
	            
	            // this is for the sum of x1 ... xn plus X0
	            for(int i = 1; i <= clst.length; i++){
		            // Set row details
	            	
	            	String cName = clst[i - 1].toString();
		            GLPK.glp_set_row_name(lp, i + clst.length, "T" + i);
		            float target = csvfHanlder.fMap.getCategoryTarget(cName);
		            GLPK.glp_set_row_bnds(lp, i + clst.length, GLPKConstants.GLP_LO, target, 0); //  T <= u
		            
		            GLPK.intArray_setitem(ind, 1, 1);
		            GLPK.doubleArray_setitem(val, 1, 1);
		            
		            for(int j = 2; j <= (flst.length + 1); j++){
			            GLPK.intArray_setitem(ind, j, j);
			            GLPK.doubleArray_setitem(val, j, csvfHanlder.fMap.getFoodCategoryValue(flst[j - 2].toString(), cName) / 100);
		            }
		            
//		            for(int j = 1;  j <= (flst.length + 1); j++){
//		            	System.out.println("pos " + GLPK.intArray_getitem(ind, j) + ": val " + GLPK.doubleArray_getitem(val, j));
//		            }
		            
		            GLPK.glp_set_mat_row(lp, i + clst.length, flst.length + 1, ind, val); // set the i row of the matrix, every time I set (flst.length + 1) numbers
	            }

	            // Define objective
	            // for the objective definition: refer http://www.doc88.com/p-09232355056.html for conversion from min{max[abs1, abs2 ... absn]}
	            //	Minimize z = x0 (x0 should be in position 1)
	            System.out.println("Set  objective");
	            GLPK.glp_set_obj_name(lp, "z");
	            GLPK.glp_set_obj_dir(lp, GLPKConstants.GLP_MIN);
	            GLPK.glp_set_obj_coef(lp, 1, 1);

	            // Solve model
	            System.out.println("Solve model");
//	            parm = new glp_smcp();
	            System.out.println("parm = new glp_smcp();");
	            GLPK.glp_init_smcp(parm);
	            ret = GLPK.glp_simplex(lp, parm);

	            // Retrieve solution
	            System.out.println("Retrieve solution");
	            if (ret == 0) {
	            	write_lp_solution(lp);
	            } else {
	                System.out.println("The problem could not be solved");
	            }

	            // Free memory
	            GLPK.delete_intArray(ind);
	            GLPK.delete_doubleArray(val);
	            
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
