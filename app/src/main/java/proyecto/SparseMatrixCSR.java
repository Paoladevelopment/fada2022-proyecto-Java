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
        //Se trabaja con LinkedLists porque no se sabe con certeza el valor fijo de los datos distintos a 0, los datos
        //se van agregando dinámicamente
        LinkedList<Integer> values= new LinkedList<Integer>();
        LinkedList<Integer> columns= new LinkedList<Integer>();
        //En el formato comprimido por filas, la lista de las filas tiene tamaño de la filas de la matrix +1
        this.rows= new int[this.matrix.length+1];
        boolean start_row= false; //indica cuando empieza una nueva fila.
        int r= 0; //iterador para la lista fila

        /**Se recorre con normalidad la matrix, solo tendremos en cuenta los valores distinto que 0, en ese caso: en la lista de columnas se agrega la columna donde hubo un valor distinto a 0.
        en la lista de valores los valores. Se va guardando en el orden normal. Es decir: si se tiene una matrix 3x3
         0 1 2
         3 0 0
         4 0 9  se guarda en el siguiente orden: [1,2,3,4,9]
         para el arreglo de filas, se usa la siguiente estrategia: con un booleano se va verificando por cada fila, que sea el inicio de una nueva fila. Es decir cuando j=0
         si se encuentra un valor distinto de 0 en la matrix, se debe verificar que sea un comienzo de fila con el booleano, para guardar en el arreglo row: el tamaño que lleva valores hasta el momento
         (se resta 1 a ese tamaño porque los arreglos son de 0 a n-1)
         ¿Por qué el tamaño de lo que lleve la lista de valores? porque así puedo saber por cada fila cuantos elementos hay y entonces cada que empiece una nueva fila tengo el registro de cuantos
         elementos hay en las filas anteriores.
         Recordar: el arreglo de filas indica la posición en la que empieza la fila.
         En este ejemplo sería row=[0,2,3,4] el ult elemento indica la finalización de las filas.
         Col= [1,2,0,0,2]
        */
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
        //en la última posición del arreglo rows se guarda el tamaño de la lista de valores.
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

    /** Para obtener un elemento: sigamos con el mismo ejemplo de matrix 3x3
     * 0 1 2
     * 3 0 0
     * 4 0 9 si se quiere consultar el valor de matrix[0][1] que es el 1
     * Se consiguen las columnas asociadas a esa fila 1: esto es averiguando en rows[i] desde que indice se empieza a consultar en el arreglo columna  y hasta donde consultar: es con rows[i+1]-1
     * Por ejemplo acá: row=[0,2,3,4] y Col= [1,2,0,0,2] si se averiguan las columnas asociadas en en la fila 0, row[0]= 0, el indice para consultar en el arreglo columna es 0 y hasta donde es: row[1]=2, hasta el indice 1.
     * col[0] y col[1], nos indica que columnas en la fila 0 tienen valores, en este caso son la columna 1 y 2.
     * por eso se realiza un for que empiece desde rows[i] hasta rows[i+1]+1, ya después se valida con un if si alguna de las columnas es igual a la columna ingresada, para así retornar el valor.
     */
    public int getElement(int i, int j) throws OperationNotSupportedException
    {
        for(int z= this.rows[i]; z < this.rows[i+1]; z++){
            if(this.columns[z] == j){
                return this.values[z];
            }
        }
        return 0;
    }

    /** Para obtener los valores de una fila
     * 0 1 2
     * 3 0 0
     * 4 0 9 ejemplo en la fila 0: 0 1 2
     * Lo que se realiza es crear un arreglo donde se almacenen estos valores, tendrá el tamaño de las dimensiones de la columna de la matrix.
     * Se averiguan los indices que se usan en el arreglo de columna con la misma estrategia que antes.
     * y la posición que muestre column[indice], será la posición de donde se guardará el valor en row_value.
     */
    public int[] getRow(int i) throws OperationNotSupportedException
    {
        int[] row_value= new int[mayor(this.columns)+1];
        for(int z= this.rows[i]; z < this.rows[i+1]; z++){
            row_value[this.columns[z]]= this.values[z];
        }

        return row_value;
    }
    /** Para obtener los valores de una columna
     * 0 1 2
     * 3 0 0
     * 4 0 9 ejemplo en la columna 0: 0 3 4
     * Lo que se realiza es crear un arreglo donde se almacenen estos valores, tendrá el tamaño de las dimensiones de la fila de la matrix.
     * Se usa un doble for, el primero se encarga de recorrer el array de filas y el segundo for se encarga de conseguir los indices para consultar en la lista de columnas
     * después se validad que column[indice] sea igual a la indicada, para ahí ir guardando los valores en el nuevo arreglo.
     */
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

    /**Para insertar un nuevo valor, creo dos nuevos arreglos para los valores y las columnas solo que van a tener una posición de más a las que ya existen.
     * se recorre el arreglo de rows de la manera que hemos recorrido en los otros métodos, para ir sabiendo en cada fila las columnas asociadas.
     * antes de hacer otros for se debe validar que donde se va a insertar el valor la fila sea igual a la ingresada (pues en esa fila se agrega el nuevo valor)
     * ya teniendo la fila, se realiza otro for para obtener los indices del arreglo de las columnas y trabajar con las columnas.
     * if(j<this.columns[z]) es para saber en que posición de la columna se puede insertar el nuevo valor. El else es simplemente para copiar de los arreglos anteriores en el nuevo los datos.
     * con el primer for se copian los datos ya existentes a los nuevos arreglos hasta agregar el nuevo valor
     */
    public void setValue(int i, int j, int value) throws OperationNotSupportedException
    {
        int[] new_values= new int[this.values.length+1];
        int[] new_columns= new int[this.columns.length+1];
        int cr=0; //iterador para la listas de columnas y valores
        this.matrix[i][j]= value;
        for (int y=0; y<this.rows.length-1;y++){
            if (y == i){
                for (int z= this.rows[y]; z<this.rows[y+1]; z++){
                    if(j < this.columns[z]){
                        new_values[cr]= value;
                        new_columns[cr]= j;
                        cr++;
                        break;
                    }else{
                        new_columns[cr]=this.columns[z];
                        new_values[cr]= this.values[z];
                        cr++;
                    }

                }
                if(j > this.columns[this.rows[y+1]-1]){
                    new_values[cr]= value;
                    new_columns[cr]= j;
                    cr++;
                    break;
                }
            }else if(y < i){
                for (int z= this.rows[y]; z<this.rows[y+1]; z++){
                    new_columns[cr]=this.columns[z];
                    new_values[cr]= this.values[z];
                    cr++;
                }
            }else {
                break;
            }
        }

        /** Este for se encarga de copiar los datos que faltan en los nuevos arreglos
         * cr es la posición que indica donde se puede seguir insertando*/
        for (int y= cr; y< new_columns.length; y++){
            new_columns[y]= this.columns[y-1];
            new_values[y]= this.values[y-1];
        }

        /**El arreglo de filas, se modifica el arreglo en el indice después a la fila dada y se le suma a 1 a los valores ya existentes */
        for (int y= i+1; y<this.rows.length;y++){
            this.rows[y]= this.rows[y]+1;
        }
        this.columns= new_columns;
        this.values=new_values;

    }

    /*
     * This method returns a representation of the Squared matrix
     * @return object that contests the squared matrix;
     */

    /**Acá únicamente se modifica el arreglo de valores elevandolos al cuadrado y se hace set al nuevo objeto CSR*/
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
    /**Se crea una nueva matrix, el tamaño de las filas será el tamaño de las columnas de la antigua matrix y el de las columnas el tamaño de las filas.
     * Se recorre cada fila y de cada fila las columnas que tengan valor asociadas a esas filas, y se intercambian filas por columnas y columnas por filas*/
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
    //mayor retorna el número mayor de un arreglo.
    private int mayor(int[] a){
        int mayor= 0;
        for (int i= 0; i<a.length;i++){
            if(a[i]>mayor){
                mayor= a[i];
            }
        }
        return mayor;
    }

    /**Realiza lo mismo que createRepresentation, solo que esta recibe una matrix*/
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
