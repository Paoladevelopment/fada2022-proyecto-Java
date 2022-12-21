package proyecto;

import javax.naming.OperationNotSupportedException;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;

import java.io.FileNotFoundException;
import java.util.LinkedList;

public class SparseMatrixCSC {
    private LoadFile loader = LoadFile.getInstance();
    @Setter
    private int[][] matrix;
    @Getter @Setter
    private int[] rows;
    @Getter @Setter
    private int[] columns;
    @Getter @Setter
    private int[] values;

    public void createRepresentation(String inputFile) throws OperationNotSupportedException, FileNotFoundException {
        //Load data
        loader.loadFile(inputFile);
        matrix = loader.getMatrix();
        LinkedList<Integer> values= new LinkedList<Integer>();
        LinkedList<Integer> rows= new LinkedList<Integer>();
        this.columns= new int[this.matrix[0].length+1];
        int cnt=0;
        for (int i=0; i<matrix[0].length; i++){
            for (int j=0; j<matrix.length; j++){
                if (matrix[j][i]!=0){
                    values.add(matrix[j][i]);
                    rows.add(j);
                    cnt++;
                }
            }
            if(i==0){
                this.columns[1]= cnt;
                cnt=0;
            }else {
                this.columns[i+1]= this.columns[i]+cnt;
                cnt=0;
            }
        }

        //Valores
        this.values= new int[values.size()];
        int i=0;
        for (int val: values){
            this.values[i]= val;
            i++;
        }

        //Filas
        this.rows= new int[rows.size()];
        i=0;
        for (int row: rows){
            this.rows[i]= row;
            i++;
        }
    }

    public int getElement(int i, int j) throws OperationNotSupportedException
    {

        for(int z= this.columns[j]; z < this.columns[j+1]; z++){
            if(this.rows[z] == i){
                return this.values[z];
            }
        }
        return 0;
    }

    public int[] getRow(int i) throws OperationNotSupportedException
    {
        int[] row_value= new int[this.columns.length-1];
        for(int j= 0; j<row_value.length; j++){
            for (int z= this.columns[j]; z <this.columns[j+1]; z++){
                if (this.rows[z] == i){
                    row_value[j]= this.values[z];
                    continue;
                }
            }
        }
        return row_value;
    }

    public int[] getColumn(int j) throws OperationNotSupportedException
    {
        int[] col_value= new int[mayor(this.rows)+1];

        for(int i=0; i<this.columns.length;i++){
            if(i == j){
                for (int z= this.columns[i]; z < this.columns[i+1];z++){
                    col_value[this.rows[z]]= this.values[z];

                }
            }
        }
        return col_value;
    }

    public void setValue(int i, int j, int value) throws OperationNotSupportedException
    {
        ArrayList<Integer> Values = new ArrayList<Integer>();
        ArrayList<Integer> Rows = new ArrayList<Integer>();

        int a = 0;
        for(int R : this.rows){
            Rows.add(R);
            a++;
        }

        a = 0;
        for(int V : this.values){
            Values.add(V);
            a++;
        }

        a = 0;
        while (a < this.columns.length){
            if (a == j){
                int lim1 = this.columns[a];
                int lim2 = this.columns[a+1]-1;
                while (lim1 <= lim2){
                    if (Rows.get(lim1) == i){
                        Values.add(lim1,value);
                        break;
                    }else{
                        lim1++;
                    }
                }
                while (a < this.columns.length-1){
                    int valor = this.columns[a+1]+1;
                    this.columns[a+1] = valor;
                    a++;
                }
                lim1 = this.columns[j];
                lim2 = this.columns[j+1]-1;
                while (lim1 <= lim2){
                    if (Rows.get(lim1) < i){
                        lim1++;
                    }else{
                        Rows.add(lim1,i);
                        Values.add(lim1, value);
                        break;
                    }
                }
                break;
            }else{
                a++;
            }
        }

        this.values = new int[Values.size()];
        this.rows = new int[Rows.size()];

        a = 0;
        for(int V : Values){
            this.values[a] = V;
            a++;
        }

        a = 0;
        for(int R : Rows){
            this.rows[a] = R;
            a++;
        }
    }

    /*
     * This method returns a representation of the Squared matrix
     * @return object that contests the squared matrix;
     */
    public SparseMatrixCSC getSquareMatrix() throws OperationNotSupportedException
    {
        SparseMatrixCSC squaredMatrix = new SparseMatrixCSC();
        squaredMatrix.setColumns(this.columns);
        squaredMatrix.setRows(this.rows);
        int[] values= new int[this.values.length];
        for (int i= 0; i<values.length; i++){
            values[i]= (int) Math.pow(this.values[i], 2);
        }
        squaredMatrix.setValues(values);
        return squaredMatrix;
    }

    /*
     * This method returns a representation of the transposed matrix
     * @return object that contests the transposed matrix;
     */
    public SparseMatrixCSC getTransposedMatrix() throws OperationNotSupportedException
    {
        SparseMatrixCSC transposedMat;
        int[][] matrix= new int[this.columns.length-1][mayor(this.rows)+1];
        for(int i= 0; i<this.columns.length-1; i++){
            for (int z= this.columns[i]; z <this.columns[i+1]; z++){
                matrix[i][this.rows[z]]= this.matrix[this.rows[z]][i];
            }
        }

        transposedMat= createRepresentationTrans(matrix);
        return transposedMat;
    }

    //Métodos útiles
    private int mayor(int[] a){
        int mayor= 0;
        for (int i= 0; i<a.length;i++){
            if(a[i]>mayor){
                mayor= a[i];
            }
        }
        return mayor;
    }

    public SparseMatrixCSC createRepresentationTrans(int[][] matrix){
        SparseMatrixCSC matCSC= new SparseMatrixCSC();
        int[] trans_values;
        int[] trans_row;
        LinkedList<Integer> values= new LinkedList<Integer>();
        LinkedList<Integer> rows= new LinkedList<Integer>();
        int[] trans_columns= new int[matrix[0].length+1];
        int cnt=0;
        for (int i=0; i<matrix[0].length; i++){
            for (int j=0; j<matrix.length; j++){
                if (matrix[j][i]!=0){
                    values.add(matrix[j][i]);
                    rows.add(j);
                    cnt++;
                }
            }
            if(i==0){
                trans_columns[1]= cnt;
                cnt=0;
            }else {
                trans_columns[i+1]= trans_columns[i]+cnt;
                cnt=0;
            }
        }

        //Valores
        trans_values= new int[values.size()];
        int i=0;
        for (int val: values){
            trans_values[i]= val;
            i++;
        }

        //Filas
        trans_row= new int[rows.size()];
        i=0;
        for (int row: rows){
            trans_row[i]= row;
            i++;
        }
        matCSC.setColumns(trans_columns);
        matCSC.setRows(trans_row);
        matCSC.setValues(trans_values);
        matCSC.setMatrix(matrix);

        return  matCSC;
    }
}
