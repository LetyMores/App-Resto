/*
	Trabajo práctico final de la Guía 6 del curso Desarrollo de Apps
	Universidad de La Punta en el marco del proyecto Argentina Programa 4.0

	Integrantes:
		John David Molina Velarde
		Leticia Mores
		Enrique Germán Martínez
		Carlos Eduardo Beltrán
 */
package vistas;

import entidades.Categoria;
import accesoadatos.CategoriaData;
import accesoadatos.CategoriaData.OrdenacionCategoria;
import java.awt.Color;
import java.util.LinkedHashMap;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *							 
 * @author John David Molina Velarde, Leticia Mores, Enrique Germán Martínez, Carlos Eduardo Beltrán
 */
public class CrudCategorias extends javax.swing.JInternalFrame {
	private CrudProductos crudProductos;
	DefaultTableModel modeloTabla;
	public static List<Categoria> listaCategorias;
	private final CategoriaData categoriaData;	

	
	private enum TipoEdicion {AGREGAR, MODIFICAR, BUSCAR};
	private TipoEdicion tipoEdicion = TipoEdicion.AGREGAR; //para que el boton guardar sepa que estoy queriendo hacer:
														   // Si con los campos voy a agregar, modificar o buscar una categoria
	private OrdenacionCategoria ordenacion = OrdenacionCategoria.PORIDCATEGORIA; // defino el tipo de orden por defecto 
	
	
	public CrudCategorias(CrudProductos crudProductos) {
		this.crudProductos = crudProductos;
		crudProductos.setVisible(false); //oculta la ventana del crud de productos
		initComponents();
		categoriaData = new CategoriaData(); 
		modeloTabla = (DefaultTableModel) tablaCategorias.getModel();
		cargarListaCategorias(); //carga la base de datos
		cargarTabla(); // cargo la tabla con las categorias
	}


	
	/** carga la lista de categorias de la BD */
	private void cargarListaCategorias(){ 
		listaCategorias = categoriaData.getListaCategorias(ordenacion);
	}
	
	
	/** carga categorias de la lista a la tabla */
	private void cargarTabla(){ 
		//borro las filas de la tabla
		for (int fila = modeloTabla.getRowCount() -  1; fila >= 0; fila--)
			modeloTabla.removeRow(fila);
		
		//cargo los categorias de listaCategorias a la tabla
		listaCategorias.stream().forEach(categoria -> 
			modeloTabla.addRow(new Object[] {
				categoria.getIdCategoria(),
				categoria.getNombre()
			}
			)
		);
		
		//como no hay fila seleccionada, deshabilito el botón Eliminar y Modificar
		if (tablaCategorias.getSelectedRow() == -1) {// si no hay alguna fila seleccionada
			btnEliminar.setEnabled(false); // deshabilito el botón de eliminar
			btnModificar.setEnabled(false); // deshabilito el botón de Modificar
		}
	} //cargarTabla
	
	
	/** 
	 * Elimina la categoria seleccionado de la lista y la bd. 
	 * @return Devuelve true si pudo eliminarlo
	 */
	private boolean eliminarCategoria(){ 
		int fila = tablaCategorias.getSelectedRow();
        if (fila != -1) { // Si hay alguna fila seleccionada
			int idCategoria = Integer.parseInt(txtIdCategoria.getText());
			if (categoriaData.bajaCategoria(idCategoria)){ 
				listaCategorias.remove(fila);
				return true;
			} else
				return false;
            //tabla.removeRowSelectionInterval(0, tabla.getRowCount()-1); //des-selecciono las filas de la tabla
        } else {
			JOptionPane.showMessageDialog(this, "Debe seleccionar una categoria para eliminar", "Ninguna categoria seleccionada", JOptionPane.ERROR_MESSAGE);
			return false;
		}
	} //eliminarCategoria
	
	
	/**
	 * si no hay errores en los campos, agrega un categoria con dichos campos. 
	 * @return Devuelve true si pudo agregarlo
	 */
	private boolean agregarCategoria(){
		Categoria categoria = campos2Categoria();
		if ( categoria != null ) {
			if ( categoriaData.altaCategoria(categoria) ) {// si pudo dar de alta al categoria
				cargarListaCategorias();
				cargarTabla();
				return true;
			} else {
				JOptionPane.showMessageDialog(this, "Debe completar correctamente todos los datos de la categoria para agregarla", "No se puede agregar", JOptionPane.ERROR_MESSAGE);
				return false;
			}
		} else {
			// si categoria es null, no pudo transformarlo a categoria. Sigo editando
			return false;
		}
	} //agregarCategoria

	
	/** si no hay errores en los campos, modifica un categoria con dichos campos */
	private void modificarCategoria() {
		Categoria categoria = campos2Categoria();
		if ( categoria != null ) {
			if ( categoriaData.modificarCategoria(categoria) )  {// si pudo  modificar al categoria
				cargarListaCategorias();
				cargarTabla();
			} else 
				JOptionPane.showMessageDialog(this, "Debe completar correctamente todos los datos de la categoria para modificarla", "No se puede modificar", JOptionPane.ERROR_MESSAGE);			
		} else {
			// si categoria es null, no pudo transformarlo a categoria. Sigo editando
		}	
	} //modificarCategoria
	
	
	
	/** deshabilito todos los botones y tabla, habilito guardar/cancelar */
	private void habilitoParaEditar(){ 
		// deshabilito todos los botones (menos salir)
		btnAgregar.setEnabled(false);
		btnModificar.setEnabled(false); //deshabilito botón modificar
		btnEliminar.setEnabled(false);  //deshabilito botón eliminar
		//btnBuscar.setEnabled(false);
		cboxOrden.setEnabled(false);
		
		//Deshabilito la Tabla para que no pueda hacer click
		tablaCategorias.setEnabled(false);
		
		//Habilito los botones guardar y cancelar
		btnGuardar.setEnabled(true); // este botón es el que realmente se encargará de agregegar el categoria
		btnCancelar.setEnabled(true);
		
		//Habilito los campos para poder editar
		txtNombre.setEditable(true);
	} //habilitoParaEditar

	
	
	
	/** habilito todos los botones y tabla, deshabilito guardar/cancelar y modificar */
	private void deshabilitoParaEditar(){ 
		limpiarCampos(); //Pongo todos los campos de texto en blanco
		// habilito todos los botones (menos salir)
		btnAgregar.setEnabled(true);
		//btnBuscar.setEnabled(true);
		cboxOrden.setEnabled(true);
		
		//sigo deshabilitando los botones modificar y eliminar porque no hay una fila seleccionada.
		btnModificar.setEnabled(false); //deshabilito botón modificar
		btnEliminar.setEnabled(false);  //deshabilito botón eliminar
		
		//Habilito la Tabla para que pueda hacer click
		tablaCategorias.setEnabled(true);
		
		//Deshabilito el boton guardar 
		btnGuardar.setEnabled(false);  
		botonGuardarComoGuardar(); //por si estaba buscando cambio icono y texto del btnGuardar a "Guardar"
		
		//deshabilito el boton cancelar
		btnCancelar.setEnabled(false);

		//deshabilito los campos para poder que no pueda editar
		txtIdCategoria.setEditable(false);
		txtNombre.setEditable(false);
	} //deshabilitoParaEditar

	
	
	
	
	/** pongo los campos txtfield en blanco y deselecciono la fila de tabla */
	private void limpiarCampos(){
		//pongo los campos en blanco
		txtIdCategoria.setText("");
		txtNombre.setText("");
		
		if (tablaCategorias.getRowCount() > 0) 
			tablaCategorias.removeRowSelectionInterval(0, tablaCategorias.getRowCount()-1); //des-selecciono las filas de la tabla
	} // limpiarCampos




	/**
	 * cargo los datos de la fila indicada de la tabla a los campos de texto de la pantalla 
	 * @param numfila el número de fila a cargar a los campos
	 */
	private void filaTabla2Campos(int numfila){
		txtIdCategoria.setText(tablaCategorias.getValueAt(numfila, 0)+"");
		txtNombre.setText(tablaCategorias.getValueAt(numfila, 1)+"");
	} //filaTabla2Campos


	
	
	/**
	 * Cargo los campos de texto de la pantalla a un objeto tipo Categoria
	 * @return El Categoria devuelto. Si hay algún error, devuelve null
	 */
	private Categoria campos2Categoria(){ 
		int idCategoria;
		String nombre;
		
		//idCategoria
		try {
			if (txtIdCategoria.getText().isEmpty()) // en el alta será un string vacío
				idCategoria = -1;
			else
				idCategoria = Integer.valueOf(txtIdCategoria.getText()); // obtengo el identificador el categoria
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this, "La IdCategoria debe ser un número válido", "IdCategoria no válida", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		
		//Nombre
		if (! txtNombre.getText().isEmpty()) // el nombre está vacío
			nombre = txtNombre.getText();
		else {		
			JOptionPane.showMessageDialog(this, "El nombre no debe estar vacío", "Nombre no válida", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		
		return new Categoria(idCategoria, nombre);
	} // campos2Categoria
	
	
	
	/** cambia el icono y texto del btnGuardar a "Guardar" */
	private void botonGuardarComoGuardar(){ 
		btnGuardar.setText("Guardar");
		btnGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/guardar1_32x32.png")));
	}	

	
	/** cambia el icono y texto del btnGuardar guardar a "Buscar" */
	private void botonGuardarComoBuscar(){ 
		btnGuardar.setText(" Buscar ");
		btnGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/buscar4_32x32.png")));
	}	

	
	
//================================================================================
//================================================================================
	
	
	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btngrpEstado = new javax.swing.ButtonGroup();
        panelCamposMesa = new javax.swing.JPanel();
        txtIdCategoria = new javax.swing.JTextField();
        btnGuardar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        panelTabla = new javax.swing.JPanel();
        lblTituloTabla = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaCategorias = new javax.swing.JTable();
        botonera = new javax.swing.JPanel();
        btnAgregar = new javax.swing.JButton();
        btnModificar = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        cboxOrden = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        btnSalir = new javax.swing.JButton();

        panelCamposMesa.setBackground(new java.awt.Color(153, 153, 255));
        panelCamposMesa.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N

        txtIdCategoria.setEditable(false);
        txtIdCategoria.setBorder(javax.swing.BorderFactory.createTitledBorder("Id Categoría"));

        btnGuardar.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/guardar1_32x32.png"))); // NOI18N
        btnGuardar.setText("Guardar");
        btnGuardar.setEnabled(false);
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
            }
        });

        btnCancelar.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/cancelar1_32x32.png"))); // NOI18N
        btnCancelar.setText("Cancelar");
        btnCancelar.setEnabled(false);
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel7.setText("Gestión de Categorías");

        txtNombre.setEditable(false);
        txtNombre.setBorder(javax.swing.BorderFactory.createTitledBorder("Nombre"));

        javax.swing.GroupLayout panelCamposMesaLayout = new javax.swing.GroupLayout(panelCamposMesa);
        panelCamposMesa.setLayout(panelCamposMesaLayout);
        panelCamposMesaLayout.setHorizontalGroup(
            panelCamposMesaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCamposMesaLayout.createSequentialGroup()
                .addGroup(panelCamposMesaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelCamposMesaLayout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addGroup(panelCamposMesaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(panelCamposMesaLayout.createSequentialGroup()
                                .addComponent(btnGuardar)
                                .addGap(18, 18, 18)
                                .addComponent(btnCancelar))
                            .addComponent(txtIdCategoria, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtNombre, javax.swing.GroupLayout.Alignment.LEADING)))
                    .addGroup(panelCamposMesaLayout.createSequentialGroup()
                        .addGap(50, 50, 50)
                        .addComponent(jLabel7)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelCamposMesaLayout.setVerticalGroup(
            panelCamposMesaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCamposMesaLayout.createSequentialGroup()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtIdCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panelCamposMesaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnGuardar)
                    .addComponent(btnCancelar))
                .addContainerGap())
        );

        panelTabla.setBackground(new java.awt.Color(153, 153, 255));

        lblTituloTabla.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lblTituloTabla.setText("Listado de Categorías");
        lblTituloTabla.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        tablaCategorias.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id", "Nombre"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tablaCategorias.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaCategoriasMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tablaCategorias);
        if (tablaCategorias.getColumnModel().getColumnCount() > 0) {
            tablaCategorias.getColumnModel().getColumn(0).setPreferredWidth(60);
            tablaCategorias.getColumnModel().getColumn(0).setMaxWidth(60);
            tablaCategorias.getColumnModel().getColumn(1).setPreferredWidth(150);
        }

        javax.swing.GroupLayout panelTablaLayout = new javax.swing.GroupLayout(panelTabla);
        panelTabla.setLayout(panelTablaLayout);
        panelTablaLayout.setHorizontalGroup(
            panelTablaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTablaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelTablaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelTablaLayout.createSequentialGroup()
                        .addComponent(lblTituloTabla)
                        .addGap(0, 167, Short.MAX_VALUE))
                    .addGroup(panelTablaLayout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        panelTablaLayout.setVerticalGroup(
            panelTablaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTablaLayout.createSequentialGroup()
                .addComponent(lblTituloTabla)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        botonera.setBackground(new java.awt.Color(153, 153, 255));

        btnAgregar.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnAgregar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/categorias2 32x32.png"))); // NOI18N
        btnAgregar.setText("Agregar");
        btnAgregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarActionPerformed(evt);
            }
        });

        btnModificar.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnModificar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/editar 1_2x32.png"))); // NOI18N
        btnModificar.setText("Modificar");
        btnModificar.setEnabled(false);
        btnModificar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnModificarActionPerformed(evt);
            }
        });

        btnEliminar.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnEliminar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/borrar2_32x32.png"))); // NOI18N
        btnEliminar.setText("Eliminar");
        btnEliminar.setEnabled(false);
        btnEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarActionPerformed(evt);
            }
        });

        cboxOrden.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "por IdCategoria", "por Nombre" }));
        cboxOrden.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboxOrdenActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel6.setText("Ordenado");

        btnSalir.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/salida1_32x32.png"))); // NOI18N
        btnSalir.setText("Salir");
        btnSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalirActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout botoneraLayout = new javax.swing.GroupLayout(botonera);
        botonera.setLayout(botoneraLayout);
        botoneraLayout.setHorizontalGroup(
            botoneraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(botoneraLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnAgregar)
                .addGap(18, 18, 18)
                .addComponent(btnModificar)
                .addGap(18, 18, 18)
                .addComponent(btnEliminar)
                .addGap(18, 18, 18)
                .addGroup(botoneraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(cboxOrden, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnSalir)
                .addContainerGap())
        );
        botoneraLayout.setVerticalGroup(
            botoneraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(botoneraLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(botoneraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(botoneraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(btnSalir, javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(botoneraLayout.createSequentialGroup()
                            .addComponent(jLabel6)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(botoneraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnAgregar)
                        .addComponent(btnModificar)
                        .addComponent(btnEliminar)
                        .addComponent(cboxOrden, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(10, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(panelCamposMesa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(botonera, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(308, 308, 308)
                .addComponent(panelTabla, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(panelTabla, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelCamposMesa, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(botonera, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

	
	
//================================================================================
//================================================================================
	
	/** permite editar en los campos, habilita boton de guardar/cancelar y deshabilita otros botones.
	    El alta verdadera lo realiza el botón de guardar (si no eligió cancelar) */
    private void btnAgregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarActionPerformed
        tipoEdicion = TipoEdicion.AGREGAR;  //para que el boton guardar sepa que estoy queriendo agregar una categoria
        limpiarCampos(); //Pongo todos los campos de texto en blanco
        habilitoParaEditar();
    }//GEN-LAST:event_btnAgregarActionPerformed

	
	/** 
	 * Permite editar en los campos, habilita boton de guardar/cancelar y deshabilita otros botones.
	 * La modificación verdadera lo realiza el botón de guardar (si no eligió cancelar)
	 */ 
    private void btnModificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModificarActionPerformed
        tipoEdicion = TipoEdicion.MODIFICAR; //para que el boton guardar sepa que estoy queriendo modificar un categoria
        habilitoParaEditar();
    }//GEN-LAST:event_btnModificarActionPerformed

	
	/** 
	 * Elimina la categoria seleccionado de la tabla. 
	 * Como no queda ninguna seleccionado, deshabilito botones btnModificar y btnEliminar
	 */
    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
        if ( eliminarCategoria() ) { // si pudo eliminar
            limpiarCampos(); //Pongo todos los campos de texto en blanco
            btnModificar.setEnabled(false); //deshabilito botón modificar
            btnEliminar.setEnabled(false);  //deshabilito botón eliminar
            cargarListaCategorias();
            cargarTabla();
        }
    }//GEN-LAST:event_btnEliminarActionPerformed

	
	
	
	
	/** Cierra la ventana (termina CrudCategorias */
    private void btnSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalirActionPerformed
        crudProductos.retornandoDeCrudCategorias(); //ejecuta refresco de listas y tablas de crud productos
		crudProductos.setVisible(true); //muestra nuevamente la ventana del crud de Productos
		
		dispose();//cierra esta ventana
    }//GEN-LAST:event_btnSalirActionPerformed

	
	
/**
 * Permite ordenar la lista de categorias por el criterio de este combo box
 * @param evt 
 */	
    private void cboxOrdenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboxOrdenActionPerformed
        if (cboxOrden.getSelectedIndex() == 0)
			ordenacion = OrdenacionCategoria.PORIDCATEGORIA;
        else if (cboxOrden.getSelectedIndex() == 1)
        ordenacion = OrdenacionCategoria.PORNOMBRE;
        else // por las dudas que no eligio uno correcto
        ordenacion = OrdenacionCategoria.PORIDCATEGORIA;

        cargarListaCategorias();
        cargarTabla();
        limpiarCampos();
        botonGuardarComoGuardar();
        deshabilitoParaEditar();
    }//GEN-LAST:event_cboxOrdenActionPerformed

	
	
	
	
	
	/** con los campos de texto de la pantalla hace un agregarCategoria, modificarCategoria o buscarCategoria
	    en base a la variable tipoEdicion, ya sea AGREGAR, MODIFICAR o BUSCAR */
    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        if ( tipoEdicion == TipoEdicion.AGREGAR ){ //agregar el categoria
            agregarCategoria();
        } else if ( tipoEdicion == TipoEdicion.MODIFICAR ) { // modificar el categoria
            modificarCategoria();
        } 

        limpiarCampos();
        botonGuardarComoGuardar();//por si estaba buscando cambio icono y texto del btnGuardar a "Guardar"
        deshabilitoParaEditar();
    }//GEN-LAST:event_btnGuardarActionPerformed

	
	
	/** Cancela la edición de campos para agregar, modificar o buscar. */
    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        limpiarCampos();
        botonGuardarComoGuardar(); //por si estaba buscando cambio icono y texto del btnGuardar a "Guardar"
        deshabilitoParaEditar();

    }//GEN-LAST:event_btnCancelarActionPerformed

	
	
	
	
	/** al hacer clik en una fila de la tabla, queda seleccionado una categoria.
	 * Entonces habilita los botones de eliminar y modificar
	 * @param evt 
	 */
    private void tablaCategoriasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaCategoriasMouseClicked
       //tabla.addRowSelectionInterval(filaTabla, filaTabla); //selecciono esa fila de la tabla
        if (tablaCategorias.getSelectedRow() != -1){ // si hay alguna fila seleccionada
		}
		int numfila = tablaCategorias.getSelectedRow();
		if (numfila != -1) {			
			btnEliminar.setEnabled(true); // habilito el botón de eliminar
			btnModificar.setEnabled(true); // habilito el botón de modificar
			
			filaTabla2Campos(numfila); // cargo los campos de texto de la pantalla con datos de la fila seccionada de la tabla
		}  
    }//GEN-LAST:event_tablaCategoriasMouseClicked


//================================================================================
//================================================================================
	
		
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel botonera;
    private javax.swing.JButton btnAgregar;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnModificar;
    private javax.swing.JButton btnSalir;
    private javax.swing.ButtonGroup btngrpEstado;
    private javax.swing.JComboBox<String> cboxOrden;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblTituloTabla;
    private javax.swing.JPanel panelCamposMesa;
    private javax.swing.JPanel panelTabla;
    private javax.swing.JTable tablaCategorias;
    private javax.swing.JTextField txtIdCategoria;
    private javax.swing.JTextField txtNombre;
    // End of variables declaration//GEN-END:variables
} // CrudCategorias


