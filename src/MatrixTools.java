import java.util.*;

public class MatrixTools {
    public static List<List<Double>> multMatrix(List<List<Double>> matrixA,List<List<Double>> matrixB){
        List<List<Double>> finalMatrix = new ArrayList<>();
        //check if matrix a columns == matrix b rows
        if (matrixA.get(0).size()!=matrixB.size())
            return finalMatrix;

        //calculate for each row in a
        for (int i=0; i<matrixA.size();i++){
            List<Double> tempList = new ArrayList<>();
            //calculate for each column in b
            for (int j=0; j<matrixB.get(0).size(); j++){
                double ans = 0.0;
                //calculate for matching col and row
                for (int k=0; k<matrixB.size(); k++)
                    ans += matrixA.get(i).get(k)*matrixB.get(k).get(j);
                tempList.add(ans);
            }
            finalMatrix.add(tempList);
        }
        return finalMatrix;
    }

    public static double euclideanDistance(List<List<Double>> a,List<List<Double>> b){
        if (a.get(0).size()!=b.get(0).size())
            return -1.0;

        double total = 0.0;
        //calculate for every matching element
        for (int i=0; i<a.get(0).size(); i++)
            total += Math.pow(a.get(0).get(i)-b.get(0).get(i),2);
        return Math.pow(total,0.5);
    }
}
