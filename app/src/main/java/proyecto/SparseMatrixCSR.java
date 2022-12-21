package proyecto;

import javax.naming.OperationNotSupportedException;
import lombok.Getter;
import lombok.Setter;

import java.io.FileNotFoundException;
import java.util.LinkedList;

public class SparseMatrixCSR {
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
        //Se trabaja con LinkedLists porque no se sabe con certeza el valor fijo de los datos distintos a 0
        LinkedList<Integer> values= new LinkedList<Integer>();
        LinkedList<Integer> columns= new LinkedList<Integer>();
        this.rows= new int[this.matrix.length+1];
        boolean start_row= false; //indica cuando empieza una nueva fila.
        int r= 0; //iterador para la lista fila
        for (int i= 0; i<matrix.length;i++){
            for (int j=0; j<matrix[0].length;j++){
                if(j==0){
                    start_row= true;
                }
                if(matrix[i][j]!=0){
                    values.add(matrix[i][j]);
                    columns.add(j);
                    if (start_row){
                        rows[r]= values.size()-1;
                        r++;
                        start_row=false;
                    }
                } else if (j== matrix[0].length-1 && start_row) {
                    rows[r]= values.size();
                    r++;
                }
            }
        }
        rows[r]= values.size();


        //Valores
        this.values= new int[values.size()];
        int i=0;
        for (int val: values){
            this.values[i]= val;
            i++;
        }

        //Columnas
        this.columns= new int[columns.size()];
        i=0;
        for (int col: columns){
            this.columns[i]= col;
            i++;
        }
    }

    public int getElement(int i, int j) throws OperationNotSupportedException
    {
        for(int z= this.rows[i]; z < this.rows[i+1]; z++){
            if(this.columns[z] == j){
                return this.values[z];
            }
        }
        return 0;
    }

    public int[] getRow(int i) throws OperationNotSupportedException
    {
        int[] row_value= new int[mayor(this.columns)+1];
        int[] indices= new int[this.rows[i+1]-this.rows[i]];
        int[] valores= new int[this.rows[i+1]-this.rows[i]];
        int r=0;
        for(int z= this.rows[i]; z < this.rows[i+1]; z++){
            indices[r]= this.columns[z];
            valores[r]= this.values[z];
            r++;
        }

        for (r=0; r<indices.length;r++){
            row_value[indices[r]]= valores[r];
        }
        return row_value;
    }

    public int[] getColumn(int j) throws OperationNotSupportedException
    {
        int[] col_value= new int[this.rows.length-1];
        for(int i= 0; i<col_value.length; i++){
            for (int z= this.rows[i]; z <this.rows[i+1]; z++){
                if (this.columns[z] == j){
                    col_value[i]= this.values[z];
                    continue;
                }
            }
        }
        return col_value;
    }

    public void setValue(int i, int j, int value) throws OperationNotSupportedException
    {
        throw new OperationNotSupportedException();
    }

    /*
     * This method returns a representation of the Squared matrix
     * @return object that contests the squared matrix;
     */
    public SparseMatrixCSR getSquareMatrix() throws OperationNotSupportedException
    {
        SparseMatrixCSR squaredMatrix = new SparseMatrixCSR();
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
    public SparseMatrixCSR getTransposedMatrix() throws OperationNotSupportedException
    {
        SparseMatrixCSR transposedMatrix;
        int[][] matrix= new int[mayor(this.columns)+1][this.rows.length-1];
        for(int i= 0; i<this.rows.length-1; i++){
            for (int z= this.rows[i]; z <this.rows[i+1]; z++){
                matrix[this.columns[z]][i]= this.matrix[i][this.columns[z]];
            }
        }
        transposedMatrix= createRepresentationTrans(matrix);
        return transposedMatrix;
    }

    //métodos útiles
    private int mayor(int[] a){
        int mayor= 0;
        for (int i= 0; i<a.length;i++){
            if(a[i]>mayor){
                mayor= a[i];
            }
        }
        return mayor;
    }

    public SparseMatrixCSR createRepresentationTrans(int[][] matrix){
        SparseMatrixCSR matCSR= new SparseMatrixCSR();
        int[] trans_col;
        int[] trans_row= new int[matrix.length+1];
        int[] trans_values;
        LinkedList<Integer> values= new LinkedList<Integer>();
        LinkedList<Integer> columns= new LinkedList<Integer>();
        boolean start_row= false; //indica cuando empieza una nueva fila.
        int r= 0; //iterador para la lista fila
        for (int i= 0; i<matrix.length;i++){
            for (int j=0; j<matrix[0].length;j++){
                if(j==0){
                    start_row= true;
                }
                if(matrix[i][j]!=0){
                    values.add(matrix[i][j]);
                    columns.add(j);
                    if (start_row){
                        trans_row[r]= values.size()-1;
                        r++;
                        start_row=false;
                    }
                } else if (j== matrix[0].length-1 && start_row) {
                    trans_row[r]= values.size();
                    r++;
                }
            }
        }
        trans_row[r]= values.size();


        //Valores
        trans_values= new int[values.size()];
        int i=0;
        for (int val: values){
            trans_values[i]= val;
            i++;
        }

        //Columnas
        trans_col= new int[columns.size()];
        i=0;
        for (int col: columns){
            trans_col[i]= col;
            i++;
        }
        matCSR.setColumns(trans_col);
        matCSR.setRows(trans_row);
        matCSR.setValues(trans_values);
        matCSR.setMatrix(matrix);
        return matCSR;
    }

}
