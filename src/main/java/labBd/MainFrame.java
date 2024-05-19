/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package labBd;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author
 */
public class MainFrame extends javax.swing.JFrame {

    // Valores para la conexión a la base de datos (su nombre, URL, Usuario y Contraseña)
    private static final String DB_NAME = "lab-bd";
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/" + DB_NAME;
    private static final String DB_USER = "postgres";
    private static final String DB_PWD = "admin";
    private String rutaArchivo = "C:\\Users\\franc\\OneDrive\\Documentos\\UNSL\\Bases de Datos\\labBd\\datos.sql";
    
    
    // Objetos utilizados para interactuar con la base de datos
    // (conexión, realizar consultas con y sin parámetros, y recibir los resultados)
    private static Connection conn = null;
    private static Statement query = null;
    private static PreparedStatement p_query = null;
    private static ResultSet result = null;



    /**
     * Creates new form MainFrame
     */
    public MainFrame() throws SQLException {
        initComponents();
        
        conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PWD);
        
        query = conn.createStatement();
        
        
        query.execute("CREATE TABLE IF NOT EXISTS Sitios("
                + "s_cod VARCHAR(50) NOT NULL, "
                + "s_localidad VARCHAR(50) NOT NULL, "
                + "PRIMARY KEY (s_cod))");
        query.execute("CREATE TABLE IF NOT EXISTS Cuadriculas("
                + "Cu_Cod VARCHAR(50) NOT NULL, "
                + "S_Cod_Dividido VARCHAR(50) NOT NULL, "
                + "PRIMARY KEY (Cu_Cod),"
                + "FOREIGN KEY (S_Cod_Dividido) REFERENCES Sitios(S_Cod))");
        query.execute("CREATE TABLE IF NOT EXISTS Cajas("
                + "Ca_Cod VARCHAR(50) NOT NULL, "
                + "Ca_Fecha VARCHAR(50) NOT NULL, "
                + "Ca_Lugar VARCHAR(50) NOT NULL, "
                + "PRIMARY KEY (Ca_Cod))");
        query.execute("CREATE TABLE IF NOT EXISTS Personas("
                + "P_Dni INT NOT NULL, "
                + "P_Nombre VARCHAR(50) NOT NULL, "
                + "p_Apellido VARCHAR(50) NOT NULL, "
                + "P_Email VARCHAR(50) NOT NULL, "
                + "P_Telefono VARCHAR(50) NOT NULL, "
                + "PRIMARY KEY (P_Dni))");
        query.execute("CREATE TABLE IF NOT EXISTS Objetos("
                + "O_Cod VARCHAR(50) NOT NULL, "
                + "O_Nombre VARCHAR(50) NOT NULL, "
                + "O_Tipoextraccion VARCHAR(50) NOT NULL, "
                + "O_Alto INT NOT NULL, "
                + "O_Largo INT NOT NULL, "
                + "O_Espesor INT NOT NULL, "
                + "O_Peso INT NOT NULL, "
                + "O_Cantidad INT NOT NULL, "
                + "O_Fecharegistro VARCHAR(50) NOT NULL, "
                + "O_Descripcion VARCHAR(50) NOT NULL, "
                + "O_Origen VARCHAR(50) NOT NULL, "
                + "CU_Cod_Asocia VARCHAR(50) NOT NULL, "
                + "Ca_Cod_Contiene VARCHAR(50) NOT NULL, "
                + "P_Dni_Ingresa INT NOT NULL, "
                + "O_Es VARCHAR(50) NOT NULL, "
                + "PRIMARY KEY (O_Cod),"
                + "FOREIGN KEY (CU_Cod_Asocia) REFERENCES Cuadriculas(Cu_Cod),"
                + "FOREIGN KEY (CA_Cod_Contiene) REFERENCES Cajas(Ca_Cod),"
                + "FOREIGN KEY (P_Dni_Ingresa) REFERENCES Personas(P_Dni))");
        query.execute("CREATE TABLE IF NOT EXISTS Liticos("
                + "O_cod VARCHAR(50) NOT NULL, "
                + "L_fechacreacion INT NOT NULL, "
                + "PRIMARY KEY (O_cod),"
                + "FOREIGN KEY (O_cod) REFERENCES Objetos(O_Cod))");
        query.execute("CREATE TABLE IF NOT EXISTS Ceramicos("
                + "O_cod VARCHAR(50) NOT NULL, "
                + "C_color VARCHAR(50) NOT NULL, "
                + "PRIMARY KEY (O_cod),"
                + "FOREIGN KEY (O_cod) REFERENCES Objetos(O_Cod))");
        
        // Inicializamos/Actualizamos la lista de personas del formulario
        // para que muestre las personas que ya están cargadas en el sistema
        updateListaResultados();
    }

    private void updateListaResultados() throws SQLException {
        
        boolean ok = false;
        
        DatabaseMetaData metaData = conn.getMetaData();
        ResultSet tables = metaData.getTables(null, "public", "%", new String[] {"TABLE"});
        
        while (tables.next()) {
            String tableName = tables.getString("TABLE_NAME");
            boolean hasData = hasData(conn, tableName);
                
            if (hasData) {
                ok = true;
            }
        }
        
        if(ok == false){
            try {
                query = conn.createStatement();

                // Abrir el archivo SQL
                BufferedReader br = new BufferedReader(new FileReader(rutaArchivo));
                String linea;

                // Leer cada línea del archivo
                while ((linea = br.readLine()) != null) {
                    // Ejecutar la sentencia SQL
                    query.execute(linea.trim());
                }
            
                try{
                    query.execute("INSERT INTO Personas (P_Dni, P_Nombre, P_Apellido, P_Email, P_Telefono) VALUES (25544555, 'Rodolphe', 'Rominov', 'rrominovm@sciencedaily.com', '7135986253')");
                    JOptionPane.showMessageDialog(null, "OK. Se ha dado de alta a Rodolphe Rominov.");
                }catch(SQLException e){
                    JOptionPane.showMessageDialog(null, "ERROR. No se ha podido dar de alta a Rodolphe Rominov");
                }
        
                try{
                    query.execute("DELETE FROM Personas WHERE P_Nombre=Benji AND P_Apellido=Colchett");
                    JOptionPane.showMessageDialog(null, "OK. Se ha dado de baja a Benji Colchett.");
                }catch(SQLException e){
                    JOptionPane.showMessageDialog(null, "ERROR. No se ha podido dar de baja a Benji Colchett.");
                }
                
                br.close();
                
            } catch (IOException | SQLException e) {
                JOptionPane.showMessageDialog(null, "ERROR. Problemas para cargar los datos desde el archivo!");
            }
        }
        else{
            JOptionPane.showMessageDialog(null, "OK. La base de datos esta cargada!");
        }
        
        result = query.executeQuery("SELECT * FROM Sitios");
        jTablaSitios.setModel(resultToTable(result));
        result = query.executeQuery("SELECT * FROM Cuadriculas");
        jTablaCuadriculas.setModel(resultToTable(result));
        result = query.executeQuery("SELECT * FROM Cajas");
        jTablaCajas.setModel(resultToTable(result));
        result = query.executeQuery("SELECT * FROM Personas");
        jTablaPersonas.setModel(resultToTable(result));
        result = query.executeQuery("SELECT * FROM Objetos");
        jTablaObjetos.setModel(resultToTable(result));
        result = query.executeQuery("SELECT * FROM Liticos");
        jTablaLiticos.setModel(resultToTable(result));
        result = query.executeQuery("SELECT * FROM Ceramicos");
        jTablaCeramicos.setModel(resultToTable(result));
    }
    
    
    
    private static boolean hasData(Connection connection, String tableName) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM " + tableName);
            resultSet.next();
            int count = resultSet.getInt(1);
            return count > 0;
        }
    }
    
    

    private static DefaultTableModel resultToTable(ResultSet rs) throws SQLException {
        
        ResultSetMetaData metaData = rs.getMetaData();
        
        Vector<String> columnNames = new Vector<String>();
        int columnCount = metaData.getColumnCount();
        for (int column = 1; column <= columnCount; column++) {
            columnNames.add(metaData.getColumnName(column));
        }
        
        Vector<Vector<Object>> data = new Vector<Vector<Object>>();
        while (rs.next()) {
            Vector<Object> vector = new Vector<Object>();
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                vector.add(rs.getObject(columnIndex));
            }
            data.add(vector);
        }

        return new DefaultTableModel(data, columnNames);
    }

    private void updateForm() throws SQLException {
        // actualizar y limpiar el formulario luego de una operación exitosa
        largo.setText("");
        //jsDniDelete.setValue(0);
        updateListaResultados();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTablaSitios = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTablaCuadriculas = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTablaCajas = new javax.swing.JTable();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTablaObjetos = new javax.swing.JTable();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTablaPersonas = new javax.swing.JTable();
        jScrollPane7 = new javax.swing.JScrollPane();
        jTablaCeramicos = new javax.swing.JTable();
        jScrollPane8 = new javax.swing.JScrollPane();
        jTablaLiticos = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        largo = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jInsertarObjeto = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        codigoObjeto = new javax.swing.JTextField();
        nombreObjeto = new javax.swing.JTextField();
        tipoExtraccion = new javax.swing.JTextField();
        espesor = new javax.swing.JTextField();
        peso = new javax.swing.JTextField();
        alto = new javax.swing.JTextField();
        fechaRegistro = new javax.swing.JTextField();
        cantidad = new javax.swing.JTextField();
        origen = new javax.swing.JTextField();
        descripcion = new javax.swing.JTextField();
        codCuadAsociado = new javax.swing.JTextField();
        es = new javax.swing.JTextField();
        codCajaContiene = new javax.swing.JTextField();
        dniIngresa = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jbEliminar = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        mostrarCajas = new javax.swing.JTable();
        jLabel17 = new javax.swing.JLabel();
        jMostrarCajas = new javax.swing.JButton();
        codigoEliminarCaja = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jMostrarCajas1 = new javax.swing.JButton();
        buscarCaja = new javax.swing.JTextField();
        jScrollPane10 = new javax.swing.JScrollPane();
        mostrarCajas2 = new javax.swing.JTable();
        buscarCajaMostrar = new javax.swing.JButton();
        jScrollPane9 = new javax.swing.JScrollPane();
        tablaObjetosMostrar = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        mesInferior = new javax.swing.JTextField();
        buscarFechas = new javax.swing.JButton();
        jScrollPane11 = new javax.swing.JScrollPane();
        objetoMostrarFechas = new javax.swing.JTable();
        anioInferior = new javax.swing.JTextField();
        diaSuperior = new javax.swing.JTextField();
        mesSuperior = new javax.swing.JTextField();
        diaInferior = new javax.swing.JTextField();
        anioSuperior = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jcantidadCeramicos = new javax.swing.JTextField();
        jcantidadLiticos = new javax.swing.JTextField();
        contarLitCer = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jTextField4 = new javax.swing.JTextField();
        contarCantidades = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Base de Datos Arqueologia");
        setBackground(new java.awt.Color(51, 51, 51));
        setMaximumSize(new java.awt.Dimension(1000, 720));
        setPreferredSize(new java.awt.Dimension(1000, 720));
        setResizable(false);

        jTabbedPane1.setBackground(new java.awt.Color(51, 51, 51));
        jTabbedPane1.setForeground(new java.awt.Color(255, 255, 255));

        jPanel1.setBackground(new java.awt.Color(51, 51, 51));

        jTablaSitios.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Codigo Sitios", "Localidad Sitios"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTablaSitios);
        jTablaSitios.getAccessibleContext().setAccessibleName("");
        jTablaSitios.getAccessibleContext().setAccessibleDescription("");

        jTablaCuadriculas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Codigo Cuadriculas", "Codigo Sitio"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(jTablaCuadriculas);

        jTablaCajas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Codigo Cajas", "Fecha Cajas", "Lugar Cajas"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(jTablaCajas);

        jTablaObjetos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Codigo Obj", "Nombre Obj", "TipoExt Obj", "Alto Obj", "Largo Obj", "Espesor Obj", "Peso Obj", "Cantidad Obj", "FechaReg Obj", "Descripcion Obj", "Origen Obj", "Cuad Codigo", "Cajas Codigo", "DNI Ingresa", "Es"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane5.setViewportView(jTablaObjetos);

        jTablaPersonas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "DNI Persona", "Nombre Persona", "Apellido Persona", "Email Persona", "Telefono Persona"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane6.setViewportView(jTablaPersonas);

        jTablaCeramicos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Codigo Obj", "Colo Ceramico"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane7.setViewportView(jTablaCeramicos);

        jTablaLiticos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Codigo Objeto", "FechaCrea Litico"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane8.setViewportView(jTablaLiticos);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 251, Short.MAX_VALUE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                        .addGap(41, 41, 41)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane6)
                            .addComponent(jScrollPane3))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane5))
                .addGap(0, 55, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 241, Short.MAX_VALUE)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 281, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Informacion", jPanel1);

        jLabel1.setText("DNI Ingresa:");

        jLabel2.setText("Nombre Objeto:");

        jLabel3.setText("Codigo Objeto:");

        jInsertarObjeto.setText("Insertar");
        jInsertarObjeto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jInsertarObjetoActionPerformed(evt);
            }
        });

        jLabel5.setText("Tipo Extraccion:");

        jLabel6.setText("Alto:");

        jLabel7.setText("Espesor:");

        jLabel8.setText("Largo:");

        jLabel9.setText("Peso:");

        jLabel10.setText("Cantidad:");

        jLabel11.setText("Fecha Registro:");

        jLabel12.setText("Descripcion:");

        jLabel13.setText("Origen:");

        jLabel14.setText("Cod Cuad Asociado:");

        jLabel15.setText("Cod Cajas Contiene:");

        jLabel16.setText("Es:");

        fechaRegistro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fechaRegistroActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel2)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addComponent(jLabel5)
                            .addComponent(jLabel8)
                            .addComponent(jLabel7)
                            .addComponent(jLabel1)
                            .addComponent(jLabel16))))
                .addGap(30, 30, 30)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(nombreObjeto, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(codigoObjeto, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tipoExtraccion, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(largo, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(espesor, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(alto, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(dniIngresa, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(110, 110, 110)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(fechaRegistro, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel15)
                                    .addComponent(jLabel14))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(codCuadAsociado, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(codCajaContiene, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(descripcion, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel9)
                                    .addComponent(jLabel10)
                                    .addComponent(jLabel12)
                                    .addComponent(jLabel13))
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGap(61, 61, 61)
                                        .addComponent(origen, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(cantidad, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(peso, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                            .addComponent(jInsertarObjeto, javax.swing.GroupLayout.Alignment.TRAILING)))
                    .addComponent(es, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(341, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(53, 53, 53)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(codigoObjeto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(peso, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(nombreObjeto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(cantidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(74, 74, 74)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(jLabel12)
                            .addComponent(alto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(descripcion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(tipoExtraccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11)
                            .addComponent(fechaRegistro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(32, 32, 32)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(largo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13)
                    .addComponent(origen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 223, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(espesor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14)
                    .addComponent(codCuadAsociado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(jLabel1)
                    .addComponent(codCajaContiene, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dniIngresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(32, 32, 32)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(es, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jInsertarObjeto)
                .addGap(27, 27, 27))
        );

        jTabbedPane1.addTab("Insertar Objeto", jPanel2);

        jLabel4.setText("Codigo de la Caja a eliminar:");

        jbEliminar.setText("Eliminar");
        jbEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbEliminarActionPerformed(evt);
            }
        });

        mostrarCajas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Codigo Caja", "Fecha Caja", "Lugar Caja"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane4.setViewportView(mostrarCajas);

        jLabel17.setText("Toque el boton Mostrar para ver las cajas cargadas");

        jMostrarCajas.setText("Mostrar");
        jMostrarCajas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMostrarCajasActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(54, 54, 54)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jbEliminar)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addComponent(jLabel4)
                            .addGap(44, 44, 44)
                            .addComponent(codigoEliminarCaja))
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addComponent(jLabel17)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jMostrarCajas))
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 423, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(523, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(jMostrarCajas))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(47, 47, 47)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(codigoEliminarCaja, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(80, 80, 80)
                .addComponent(jbEliminar)
                .addContainerGap(266, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Eliminar Caja", jPanel3);

        jLabel18.setText("Ingrese el codigo de la caja:");

        jLabel19.setText("Toque el boton Mostrar para ver las cajas cargadas");

        jMostrarCajas1.setText("Mostrar");
        jMostrarCajas1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMostrarCajas1ActionPerformed(evt);
            }
        });

        buscarCaja.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buscarCajaActionPerformed(evt);
            }
        });

        mostrarCajas2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Codigo Caja", "Fecha Caja", "Lugar Caja"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane10.setViewportView(mostrarCajas2);

        buscarCajaMostrar.setText("Buscar");
        buscarCajaMostrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buscarCajaMostrarActionPerformed(evt);
            }
        });

        tablaObjetosMostrar.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Codigo Obj", "Nombre Obj", "TipoExt Obj", "Alto Obj", "Largo Obj", "Espesor Obj", "Peso Obj", "Cantidad Obj", "FechaReg Obj", "Descripcion Obj", "Origen Obj", "Cuad Codigo", "Cajas Codigo", "DNI Ingresa", "Es"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane9.setViewportView(tablaObjetosMostrar);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(407, 407, 407)
                        .addComponent(jMostrarCajas1))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(68, 68, 68)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel18)
                                .addGap(54, 54, 54)
                                .addComponent(buscarCaja, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(62, 62, 62)
                                .addComponent(buscarCajaMostrar))
                            .addComponent(jLabel19)
                            .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 755, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(177, Short.MAX_VALUE))
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel4Layout.createSequentialGroup()
                    .addGap(66, 66, 66)
                    .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 749, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(185, Short.MAX_VALUE)))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(jMostrarCajas1))
                .addGap(144, 144, 144)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(buscarCaja, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buscarCajaMostrar))
                .addGap(38, 38, 38)
                .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(125, Short.MAX_VALUE))
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel4Layout.createSequentialGroup()
                    .addGap(86, 86, 86)
                    .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(493, Short.MAX_VALUE)))
        );

        jTabbedPane1.addTab("Obtener Objetos", jPanel4);

        jLabel20.setText("Fecha Inferior:");

        jLabel21.setText("Fecha Superior:");

        buscarFechas.setText("Buscar");
        buscarFechas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buscarFechasActionPerformed(evt);
            }
        });

        objetoMostrarFechas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Codigo Objeto", "Nombre Objeto"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane11.setViewportView(objetoMostrarFechas);

        jLabel22.setText("dd-mm-aaaa");

        jLabel23.setText("dd-mm-aaaa");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(71, 71, 71)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(buscarFechas)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel5Layout.createSequentialGroup()
                            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel21)
                                .addComponent(jLabel20))
                            .addGap(27, 27, 27)
                            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(diaSuperior, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                                .addComponent(diaInferior))
                            .addGap(18, 18, 18)
                            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(mesSuperior, javax.swing.GroupLayout.DEFAULT_SIZE, 73, Short.MAX_VALUE)
                                .addComponent(mesInferior))
                            .addGap(18, 18, 18)
                            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(anioInferior)
                                .addComponent(anioSuperior))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel22)
                                .addComponent(jLabel23)))))
                .addGap(477, 477, 477))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel20)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(mesInferior, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(anioInferior, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(diaInferior, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel22)))
                .addGap(19, 19, 19)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(diaSuperior, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mesSuperior, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(anioSuperior, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23))
                .addGap(26, 26, 26)
                .addComponent(buscarFechas)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, 283, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(240, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Obtener Objetos Fechas", jPanel5);

        jLabel24.setText("Cantidad de Litico:");

        jLabel25.setText("Cantidad de Ceramico:");

        contarLitCer.setText("Contar");
        contarLitCer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                contarLitCerActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(85, 85, 85)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(contarLitCer)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel25)
                            .addComponent(jLabel24))
                        .addGap(59, 59, 59)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jcantidadLiticos, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jcantidadCeramicos, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(574, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(64, 64, 64)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel24)
                    .addComponent(jcantidadLiticos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(53, 53, 53)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel25)
                    .addComponent(jcantidadCeramicos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(59, 59, 59)
                .addComponent(contarLitCer)
                .addContainerGap(445, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Cantidad Lit y Cer", jPanel6);

        jLabel26.setText("Cantidad de Personas:");

        jLabel27.setText("Cantidad de Objetos:");

        jLabel28.setText("Cantidad de Cuadriculas:");

        jLabel29.setText("Cantidad de Cajas:");

        contarCantidades.setText("Cantidades");
        contarCantidades.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                contarCantidadesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(105, 105, 105)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel26)
                    .addComponent(jLabel28)
                    .addComponent(jLabel27)
                    .addComponent(jLabel29))
                .addGap(90, 90, 90)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(contarCantidades)
                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(584, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(54, 54, 54)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel26)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(40, 40, 40)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel28)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(43, 43, 43)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel27)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(47, 47, 47)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel29)
                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(62, 62, 62)
                .addComponent(contarCantidades)
                .addContainerGap(331, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Cantidades", jPanel7);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1000, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );

        jTabbedPane1.getAccessibleContext().setAccessibleName("tab_panel");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jInsertarObjetoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jInsertarObjetoActionPerformed
        
        
        int ok = 1;
        
        if(codigoObjeto.getText().trim().equals("")){
            ok = 0;
        }
        
        if(nombreObjeto.getText().trim().equals("") || nombreObjeto.getText().matches("\\d+")){
            ok = 0;
        }
        
        if(tipoExtraccion.getText().trim().equals("")){
            ok = 0;
        }
        
        if(alto.getText().trim().equals("") ){  //|| altoFloat < 0
            ok = 0;
        }
        else{
           float altoFloat = Float.parseFloat(alto.getText().trim());
           if(altoFloat < 0){
               ok = 0;
           }
        }
        
        if (largo.getText().trim().equals("") ) {  //|| largoFloat < 0
            ok = 0;
        }
        else{
            float largoFloat = Float.parseFloat(largo.getText().trim());
            if(largoFloat < 0){
                ok = 0;
            }
        }
        
        if(espesor.getText().trim().equals("") ){  //|| espesorFloat < 0
            ok = 0;
        }
        else{
            float espesorFloat = Float.parseFloat(espesor.getText().trim());
            if(espesorFloat < 0){
                ok = 0;
            }
        }
        
        if (peso.getText().trim().equals("") ) {   //|| pesoFloat < 0
            ok = 0;
        }
        else{
            float pesoFloat = Float.parseFloat(peso.getText().trim());
            if(pesoFloat < 0){
                ok = 0;
            }
        }
        
        if(cantidad.getText().trim().equals("") ){   //|| cantidadInt < 0
            ok = 0;
        }
        else{
            int cantidadInt = Integer.parseInt(cantidad.getText().trim());
            if(cantidadInt < 0){
                ok = 0;
            }
        }
        
        if(fechaRegistro.getText().trim().equals("")){
            ok = 0;
        }
        
        if(descripcion.getText().trim().equals("")){
            ok = 0;
        }
        
        if(origen.getText().trim().equals("")){
            ok = 0;
        }
        
        if(codCuadAsociado.getText().trim().equals("")){
            ok = 0;
        }
        
        if(codCajaContiene.getText().trim().equals("")){
            ok = 0;
        }
        
        if(dniIngresa.getText().trim().equals("") ){   //|| dniInt < 0
            ok = 0;
        }
        else{
            int dniInt = Integer.parseInt(dniIngresa.getText().trim());
            if(dniInt < 0){
                ok = 0;
            }
        }
        
        if(!es.getText().trim().equals("L") || es.getText().trim().equals("")){
            if(es.getText().trim().equals("C")){
                ok = 0;
            }
        }
        
        
        if(ok == 1){
            
            try {
                p_query = conn.prepareStatement("INSERT INTO Objetos (O_Cod, O_Nombre, O_Tipoextraccion, O_Alto, O_Largo, O_Espesor, O_Peso, O_Cantidad, O_Fecharegistro, O_Descripcion, O_Origen, CU_Cod_Asocia, Ca_Cod_Contiene, P_Dni_Ingresa, O_Es) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

                
                p_query.setString(1, codigoObjeto.getText().trim());
                p_query.setString(2, nombreObjeto.getText().trim());
                p_query.setString(3, tipoExtraccion.getText().trim());
                p_query.setFloat(4, Float.parseFloat(alto.getText().trim()));
                p_query.setFloat(5, Float.parseFloat(largo.getText().trim()));
                p_query.setFloat(6, Float.parseFloat(espesor.getText().trim()));
                p_query.setFloat(7, Float.parseFloat(peso.getText().trim()));
                p_query.setInt(8, Integer.parseInt(cantidad.getText().trim()));
                p_query.setString(9, fechaRegistro.getText().trim());
                p_query.setString(10, descripcion.getText().trim());
                p_query.setString(11, origen.getText().trim());
                p_query.setString(12, codCuadAsociado.getText().trim());
                p_query.setString(13, codCajaContiene.getText().trim());
                p_query.setInt(14, Integer.parseInt(dniIngresa.getText().trim()));
                p_query.setString(15, es.getText().trim());
                
                p_query.executeUpdate();
                JOptionPane.showMessageDialog(null, "OK. Se han cargado los datos correctamente!");
                updateForm();
            } catch (Exception ex) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else{
            JOptionPane.showMessageDialog(null, "ERROR. Verifique que los datos sean correctos!");
        }
    }//GEN-LAST:event_jInsertarObjetoActionPerformed

    private void jbEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbEliminarActionPerformed
        
        if(codigoEliminarCaja.getText().trim().equals("")){
            JOptionPane.showMessageDialog(null, "ERROR. No ingreso ningun codigo");
        }
        else{
            String codigo = codigoEliminarCaja.getText().trim();
       
            int respuesta = JOptionPane.showConfirmDialog(null, "¿Está seguro que desea eliminar la Caja?", "Confirmar acción", JOptionPane.YES_NO_OPTION);
        
            if(respuesta == JOptionPane.YES_OPTION){
                try {
                    
                    // Eliminar filas de la tabla Ceramicos con los códigos de objetos que se eliminarán
                    p_query = conn.prepareStatement("DELETE FROM Ceramicos WHERE O_cod IN (SELECT O_Cod FROM Objetos WHERE Ca_Cod_Contiene = ?)");
                    p_query.setString(1, codigo);
                    p_query.executeUpdate();
                    
                    // Eliminar filas de la tabla Liticos con los códigos de objetos que se eliminarán
                    p_query = conn.prepareStatement("DELETE FROM Liticos WHERE O_cod IN (SELECT O_Cod FROM Objetos WHERE Ca_Cod_Contiene = ?)");
                    p_query.setString(1, codigo);
                    p_query.executeUpdate();
                    
                    // Eliminar filas de la tabla Objetos con el código de caja que deseas eliminar
                    p_query = conn.prepareStatement("DELETE FROM Objetos WHERE Ca_Cod_Contiene = ?");
                    p_query.setString(1, codigo);
                    p_query.executeUpdate();

                    // Finalmente, eliminar la fila de la tabla Cajas con el código que deseas eliminar
                    p_query = conn.prepareStatement("DELETE FROM Cajas WHERE Ca_Cod = ?");
                    p_query.setString(1, codigo);
                    p_query.executeUpdate();

                    JOptionPane.showMessageDialog(null, "La caja y sus objetos asociados han sido eliminados exitosamente.");
                    updateForm();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error al intentar eliminar la caja y sus objetos asociados.");
                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }//GEN-LAST:event_jbEliminarActionPerformed

    private void fechaRegistroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fechaRegistroActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_fechaRegistroActionPerformed

    private void jMostrarCajasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMostrarCajasActionPerformed
        // TODO add your handling code here:
        try{
            result = query.executeQuery("SELECT * FROM Cajas");
            mostrarCajas.setModel(resultToTable(result));
        }catch(SQLException e){
            JOptionPane.showMessageDialog(null, "ERROR. No se pudo obtener datos!");
        }
    }//GEN-LAST:event_jMostrarCajasActionPerformed

    private void jMostrarCajas1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMostrarCajas1ActionPerformed
        // TODO add your handling code here:
        try{
            result = query.executeQuery("SELECT * FROM Cajas");
            mostrarCajas2.setModel(resultToTable(result));
        }catch(SQLException e){
            JOptionPane.showMessageDialog(null, "ERROR. No se pudo obtener datos!");
        }
    }//GEN-LAST:event_jMostrarCajas1ActionPerformed

    private void buscarCajaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buscarCajaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_buscarCajaActionPerformed

    private void buscarCajaMostrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buscarCajaMostrarActionPerformed
        // TODO add your handling code here:
        if(buscarCaja.getText().trim().equals("")){
            JOptionPane.showMessageDialog(null, "ERROR. Ingrese un codigo por favor!");
        }
        else{
            try{
                String codigo = buscarCaja.getText().trim();
                p_query = conn.prepareStatement("SELECT * FROM Objetos WHERE Ca_Cod_Contiene = ?");
                p_query.setString(1, codigo);
                result = p_query.executeQuery();
                tablaObjetosMostrar.setModel(resultToTable(result));
            }catch(SQLException e){
                JOptionPane.showMessageDialog(null, "ERROR!");
            }
        }
    }//GEN-LAST:event_buscarCajaMostrarActionPerformed

    private void buscarFechasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buscarFechasActionPerformed
        int ok = 0;
        int conf = 0;
        
        if(!diaInferior.getText().trim().equals("") && !mesInferior.getText().trim().equals("") 
            && !anioInferior.getText().trim().equals("") && !diaSuperior.getText().trim().equals("") 
            && !mesSuperior.getText().trim().equals("") && !anioSuperior.getText().trim().equals("")){
            ok = 1;
        }
        
        if(ok == 1){
            int diaI = Integer.parseInt(diaInferior.getText().trim());
            int mesI = Integer.parseInt(mesInferior.getText().trim());
            int anioI = Integer.parseInt(anioInferior.getText().trim());
            int diaS = Integer.parseInt(diaSuperior.getText().trim());
            int mesS = Integer.parseInt(mesSuperior.getText().trim());
            int anioS = Integer.parseInt(anioSuperior.getText().trim());
            
            if(diaI < 0 || diaS < 0 || mesI < 0 || mesS < 0 || anioI < 0 || anioS < 0){
                JOptionPane.showMessageDialog(null, "ERROR. Datos incorrectos, verifique!");
            }
            else{
                if(diaI > 31 || diaS > 31 || mesI > 12 || mesS > 12){
                    JOptionPane.showMessageDialog(null, "ERROR. Datos incorrectos, verifique!");
                }
                else{
                    if(anioI <= anioS){
                        if(mesI < mesS){
                            String fechaInferior = String.valueOf(diaI) + "-" + String.valueOf(mesI) + "-" + String.valueOf(anioI);
                            String fechaSuperior = String.valueOf(diaS) + "-" + String.valueOf(mesS) + "-" + String.valueOf(anioS);
                            try{
                                p_query = conn.prepareStatement("SELECT O_Cod, O_Nombre FROM Objetos WHERE O_Fecharegistro BETWEEN ? AND ?");
                                p_query.setString(1, fechaInferior);
                                p_query.setString(2, fechaSuperior);
                                result = p_query.executeQuery();
                                objetoMostrarFechas.setModel(resultToTable(result));
                            }catch(SQLException e){
                                JOptionPane.showMessageDialog(null, "ERROR!");
                            }
                        }
                        else{
                            if(mesI == mesS){
                                if(diaI <= diaS){
                                    String fechaInferior = String.valueOf(diaI) + "-" + String.valueOf(mesI) + "-" + String.valueOf(anioI);
                                    String fechaSuperior = String.valueOf(diaS) + "-" + String.valueOf(mesS) + "-" + String.valueOf(anioS);
                                    try{
                                        p_query = conn.prepareStatement("SELECT O_Cod, O_Nombre FROM Objetos WHERE O_Fecharegistro BETWEEN ? AND ?");
                                        p_query.setString(1, fechaInferior);
                                        p_query.setString(1, fechaSuperior);
                                        result = p_query.executeQuery();
                                        objetoMostrarFechas.setModel(resultToTable(result));
                                    }catch(SQLException e){
                                        JOptionPane.showMessageDialog(null, "ERROR!");
                                    }
                                }
                                else{
                                    JOptionPane.showMessageDialog(null, "ERROR. Datos incorrectos, verifique!");
                                }
                            }
                            else{
                                JOptionPane.showMessageDialog(null, "ERROR. Datos incorrectos, verifique!");
                            }
                        }
                    }
                    else{
                        JOptionPane.showMessageDialog(null, "ERROR. Datos incorrectos, verifique!");
                    }
                }
            }
        }
        else{
           JOptionPane.showMessageDialog(null, "ERROR. Ingrese datos por favor!"); 
        }
    }//GEN-LAST:event_buscarFechasActionPerformed

    private void contarLitCerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_contarLitCerActionPerformed
        // TODO add your handling code here:
        try{
            p_query = conn.prepareStatement("SELECT COUNT(*) AS cantidad_liticos FROM Liticos");
            result = p_query.executeQuery();
            if(result.next()){
                int cantidadLiticos = result.getInt("cantidad_liticos");
                cantidadLiticos += 1;
                String cantL = String.valueOf(cantidadLiticos);
                jcantidadLiticos.setText(cantL);
            }
            p_query = conn.prepareStatement("SELECT COUNT(*) AS cantidad_ceramicos FROM Ceramicos");
            result = p_query.executeQuery();
            if(result.next()){
                int cantidadCeramicos = result.getInt("cantidad_ceramicos");
                cantidadCeramicos += 1;
                String cantC = String.valueOf(cantidadCeramicos);
                jcantidadCeramicos.setText(cantC);
            }
        }catch(SQLException e){
            JOptionPane.showMessageDialog(null, "ERROR!"); 
        }
    }//GEN-LAST:event_contarLitCerActionPerformed

    private void contarCantidadesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_contarCantidadesActionPerformed
        
        try{
            p_query = conn.prepareStatement("SELECT COUNT(*) AS cantidad_liticos FROM Liticos");
            result = p_query.executeQuery();
            if(result.next()){
                int cantidadLiticos = result.getInt("cantidad_liticos");
                cantidadLiticos += 1;
                String cantL = String.valueOf(cantidadLiticos);
                jcantidadLiticos.setText(cantL);
            }
            p_query = conn.prepareStatement("SELECT COUNT(*) AS cantidad_ceramicos FROM Ceramicos");
            result = p_query.executeQuery();
            if(result.next()){
                int cantidadCeramicos = result.getInt("cantidad_ceramicos");
                cantidadCeramicos += 1;
                String cantC = String.valueOf(cantidadCeramicos);
                jcantidadCeramicos.setText(cantC);
            }
        }catch(SQLException e){
            JOptionPane.showMessageDialog(null, "ERROR!"); 
        }
        
    }//GEN-LAST:event_contarCantidadesActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new MainFrame().setVisible(true);
                } catch (SQLException ex) {
                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField alto;
    private javax.swing.JTextField anioInferior;
    private javax.swing.JTextField anioSuperior;
    private javax.swing.JTextField buscarCaja;
    private javax.swing.JButton buscarCajaMostrar;
    private javax.swing.JButton buscarFechas;
    private javax.swing.JTextField cantidad;
    private javax.swing.JTextField codCajaContiene;
    private javax.swing.JTextField codCuadAsociado;
    private javax.swing.JTextField codigoEliminarCaja;
    private javax.swing.JTextField codigoObjeto;
    private javax.swing.JButton contarCantidades;
    private javax.swing.JButton contarLitCer;
    private javax.swing.JTextField descripcion;
    private javax.swing.JTextField diaInferior;
    private javax.swing.JTextField diaSuperior;
    private javax.swing.JTextField dniIngresa;
    private javax.swing.JTextField es;
    private javax.swing.JTextField espesor;
    private javax.swing.JTextField fechaRegistro;
    private javax.swing.JButton jInsertarObjeto;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JButton jMostrarCajas;
    private javax.swing.JButton jMostrarCajas1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTablaCajas;
    private javax.swing.JTable jTablaCeramicos;
    private javax.swing.JTable jTablaCuadriculas;
    private javax.swing.JTable jTablaLiticos;
    private javax.swing.JTable jTablaObjetos;
    private javax.swing.JTable jTablaPersonas;
    private javax.swing.JTable jTablaSitios;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JButton jbEliminar;
    private javax.swing.JTextField jcantidadCeramicos;
    private javax.swing.JTextField jcantidadLiticos;
    private javax.swing.JTextField largo;
    private javax.swing.JTextField mesInferior;
    private javax.swing.JTextField mesSuperior;
    private javax.swing.JTable mostrarCajas;
    private javax.swing.JTable mostrarCajas2;
    private javax.swing.JTextField nombreObjeto;
    private javax.swing.JTable objetoMostrarFechas;
    private javax.swing.JTextField origen;
    private javax.swing.JTextField peso;
    private javax.swing.JTable tablaObjetosMostrar;
    private javax.swing.JTextField tipoExtraccion;
    // End of variables declaration//GEN-END:variables
}
