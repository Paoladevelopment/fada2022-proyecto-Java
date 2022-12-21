package proyecto;

import javax.naming.OperationNotSupportedException;
import lombok.Getter;
import lombok.Setter;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;

public class SparseMatrixCoordinateFormat {

    private LoadFile loader = LoadFile.getInstance();
    @Setter
    private int[][] matrix;
    @Getter
    @Setter
    private int[] rows;
    @Getter
    @Setter
    private int[] columns;
    @Getter
    @Setter
    private int[] values;

    public void createRepresentation(String inputFile) throws OperationNotSupportedException, FileNotFoundException {
        //Load data
        loader.loadFile(inputFile);
        matrix = loader.getMatrix();
        //Se trabaja con LinkedLists porque no se sabe con certeza el valor fijo de los datos distintos a 0
        LinkedList<Integer> values= new LinkedList<Integer>();
        LinkedList<Integer> rows= new LinkedList<Integer>();
        LinkedList<Integer> columns= new LinkedList<Integer>();
        for (int i= 0; i<matrix.length;i++){
            for (int j=0; j<matrix[0].length;j++){
                if(matrix[i][j]!=0){
                    values.add(matrix[i][j]);
                    rows.add(i);
                    columns.add(j);
                }
            }
        }

        //Elementos de values en el atributo value de la clase del formato coordenado
        //Valores
        this.values= new int[values.size()];
        int i=0;
        for (int val: values){
            this.values[i]= val;
            i++;
        }

        //filas
        this.rows= new int[rows.size()];
        i=0;
        for (int fila: rows){
            this.rows[i]= fila;
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
        //No usar this.matrix aqui.
        for(int z = 0; z < this.values.length;z++){
            if(this.rows[z]== i && this.columns[z] == j){
                return this.values[z];
            }
        }
        return 0;
    }

    public int[] getRow(int i) throws OperationNotSupportedException
    {
        int[] row_values= new int[mayor(this.columns)+1];
        int j=0;
        int i_row=0;
        while (j < this.rows.length){
            if(this.rows[j] == i){
                i_row= this.columns[j];
                row_values[i_row]= this.values[j];
            }
            j++;
        }




        return row_values;
    }

    public int[] getColumn(int j) throws OperationNotSupportedException
    {
        //No usar this.matrix aqui.
        int[] col_values= new int[mayor(this.rows)+1];
        int i=0;
        int i_col=0;
        while (i < this.columns.length){
            if(this.columns[i] == j){
                i_col= this.rows[i];
                col_values[i_col]= this.values[i];
            }
            i++;
        }
        return  col_values;
    }

    public void setValue(int i, int j, int value) throws OperationNotSupportedException
    {
        //Cambiar los atributos rows, cols, values y matrix aqui
        this.matrix[i][j]= value;
        int[] new_col= new int[this.columns.length+1];
        int[] new_fil= new int[this.rows.length+1];
        int[] new_value= new int[this.values.length+1];

        //Llenar las nuevas listas, añadiendo el dato ingresado
        int cnt=0; //para saber cuantos elementos hay en la fila que se va a insertar el nuevo dato
        int whereBegins=0;
        for(int r= 0; r<this.rows.length;r++){
            if(this.rows[r]== i){
                cnt++;
                if(cnt==1){
                    whereBegins= r;
                }
            }
        }

        int location_dato=whereBegins;
        for (int r=whereBegins; r<whereBegins+cnt; r++){
            if (location_dato<this.columns[r]){
                location_dato= this.columns[r];
            }
        }
        new_fil[location_dato]= i;
        new_col[location_dato]=j;
        new_value[location_dato]= value;

        for (int r= 0; r<location_dato;r++){
            new_fil[r]= this.rows[r];
            new_col[r]=this.columns[r];
            new_value[r]= this.values[r];
        }

        for (int r= location_dato+1; r<=this.rows.length; r++){
            new_fil[r]= this.rows[r-1];
            new_col[r]=this.columns[r-1];
            new_value[r]= this.values[r-1];
        }

        this.rows= new_fil;
        this.columns= new_col;
        this.values= new_value;



    }

    /*
    * This method returns a representation of the Squared matrix
    * @return object that contests the squared matrix;
     */
    public SparseMatrixCoordinateFormat getSquareMatrix() throws OperationNotSupportedException
    {
        SparseMatrixCoordinateFormat squaredMatrix = new SparseMatrixCoordinateFormat();
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
    public SparseMatrixCoordinateFormat getTransposedMatrix() throws OperationNotSupportedException
    {
        SparseMatrixCoordinateFormat transposedMatrix = new SparseMatrixCoordinateFormat();
        //Usar los metodos Set aqui de los atributos
        int[] trans_row= new int[this.rows.length];
        int[] trans_col= new  int[this.columns.length];
        int[] values= new int[this.values.length];
        int[][] matrix= new int[mayor(this.columns)+1][mayor(this.rows)+1];
        for (int i= 0; i< matrix.length; i++){
            matrix[this.columns[i]][this.rows[i]]= this.matrix[this.rows[i]][this.columns[i]];

        }
        transposedMatrix.setMatrix(matrix);
        int z=0; //iterador para las listas de columnas y filas
        for (int i= 0; i< matrix.length; i++){
            for (int j= 0; j< matrix[0].length; j++){
                if (matrix[i][j] !=0){
                    trans_row[z]=i;
                    trans_col[z]=j;
                    values[z]= matrix[i][j];
                    z++;
                }
            }
        }
        transposedMatrix.setRows(trans_row);
        transposedMatrix.setColumns(trans_col);
        transposedMatrix.setValues(values);
        return  transposedMatrix;
    }


    //Método de utilidad
    private int mayor(int[] a){
        int mayor= 0;
        for (int i= 0; i<a.length;i++){
            if(a[i]>mayor){
                mayor= a[i];
            }
        }
        return mayor;
    }



}
