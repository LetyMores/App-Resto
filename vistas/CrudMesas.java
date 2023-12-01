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

import entidades.Mesa;
import accesoadatos.MesaData;
import accesoadatos.MesaData.OrdenacionMesa;
import entidades.Servicio;
import accesoadatos.ServicioData;
import utiles.Utils;
import java.awt.Color;
import java.util.LinkedHashMap;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *							 
 * @author John David Molina Velarde, Leticia Mores, Enrique Germán Martínez, Carlos Eduardo Beltrán
 */
public class CrudMesas extends javax.swing.JInternalFrame {
	LinkedHashMap<Integer, Servicio> mapaMeseros = new LinkedHashMap<>();
	Servicio servicioSinAsignar = new Servicio(0, "SIN ASIGNAR", "", 0, Servicio.TipoServicio.MESERO, "");
	DefaultTableModel modeloTabla;
	public static List<Mesa> listaMesas;
	private final MesaData mesaData;	
	private enum TipoEdicion {AGREGAR, MODIFICAR, BUSCAR};
	private TipoEdicion tipoEdicion = TipoEdicion.AGREGAR; //para que el boton guardar sepa que estoy queriendo hacer:
														   // Si con los campos voy a agregar, modificar o buscar una mesa
	private OrdenacionMesa ordenacion = OrdenacionMesa.PORIDMESA; // defino el tipo de orden por defecto 
	private FiltroMesas filtro = new FiltroMesas();  //el filtro de búsqueda
	
	
	public CrudMesas() {
		initComponents();
		cargarMapaMeseros();
		mesaData = new MesaData(); 
		modeloTabla = (DefaultTableModel) tablaMesas.getModel();
		cargarListaMesas(); //carga la base de datos
		cargarTabla(); // cargo la tabla con las mesas
	}

	/**
	 * Carga la lista de meseros de la tabla de Servicios y también los agrega al combo box
	 */
	private void cargarMapaMeseros(){
		// cargo la lista de meseros
		ServicioData servicioData = new ServicioData();
		List<Servicio> listaMeseros = servicioData.getListaServiciosXCriterioDeBusqueda(
			//idServicio, nombre, host, puerto, Servicio.TipoServicio,		  ordenacion
			-1,			  "",	  "",   -1,     Servicio.TipoServicio.MESERO, ServicioData.OrdenacionServicio.PORIDSERVICIO);
		listaMeseros.add(0, servicioSinAsignar);// para cuando no hay un mesero asignado a la mesa.
		
		//copio esa lista de meseros a un mapa
		mapaMeseros = new LinkedHashMap();
		listaMeseros.stream().forEach( mesero -> mapaMeseros.put(mesero.getIdServicio(), mesero) );
		
		//esa lista de meseros lo cargo al JComboBox cbIdNombreMesero
		listaMeseros.stream().forEach( mesero -> 
			cbIdNombreMesero.addItem( mesero ) 
		);
	}
	
	
	
	/** carga la lista de mesas de la BD */
	private void cargarListaMesas(){ 
		if (filtro.estoyFiltrando) 
			listaMesas = mesaData.getListaMesasXCriterioDeBusqueda(filtro.idMesa, filtro.capacidad, filtro.estado, filtro.idMesero, ordenacion);
		else
			listaMesas = mesaData.getListaMesas(ordenacion);
	}
	
	
	/** carga mesas de la lista a la tabla */
	private void cargarTabla(){ 
		//borro las filas de la tabla
		for (int fila = modeloTabla.getRowCount() -  1; fila >= 0; fila--)
			modeloTabla.removeRow(fila);
		
		//cargo los mesas de listaMesas a la tabla
		for (Mesa mesa : listaMesas) {
			modeloTabla.addRow(new Object[] {
				mesa.getIdMesa(),
				mesa.getCapacidad(),
				mesa.getEstado(),
				mapaMeseros.get(mesa.getIdMesero()) //almaceno el objeto Servicio del mesero idMesero
			}
			);
		}
		
		//como no hay fila seleccionada, deshabilito el botón Eliminar y Modificar
		if (tablaMesas.getSelectedRow() == -1) {// si no hay alguna fila seleccionada
			btnEliminar.setEnabled(false); // deshabilito el botón de eliminar
			btnModificar.setEnabled(false); // deshabilito el botón de Modificar
		}
	} //cargarTabla
	
	
	/** 
	 * Elimina al mesa seleccionado de la lista y la bd. 
	 * @return Devuelve true si pudo eliminarlo
	 */
	private boolean eliminarMesa(){ 
		int fila = tablaMesas.getSelectedRow();
        if (fila != -1) { // Si hay alguna fila seleccionada
			int idMesa = Integer.parseInt(txtIdMesa.getText());
			if (mesaData.bajaMesa(idMesa)){ 
				listaMesas.remove(fila);
				return true;
			} else
				return false;
            //tabla.removeRowSelectionInterval(0, tabla.getRowCount()-1); //des-selecciono las filas de la tabla
        } else {
			JOptionPane.showMessageDialog(this, "Debe seleccionar una mesa para eliminar", "Ninguna mesa seleccionado", JOptionPane.ERROR_MESSAGE);
			return false;
		}
	} //eliminarMesa
	
	
	/**
	 * si no hay errores en los campos, agrega un mesa con dichos campos. 
	 * @return Devuelve true si pudo agregarlo
	 */
	private boolean agregarMesa(){
		Mesa mesa = campos2Mesa();
		if ( mesa != null ) {
			if ( mesaData.altaMesa(mesa) ) {// si pudo dar de alta al mesa
				cargarListaMesas();
				cargarTabla();
				return true;
			} else {
				JOptionPane.showMessageDialog(this, "Debe completar correctamente todos los datos de la mesa para agregarla", "No se puede agregar", JOptionPane.ERROR_MESSAGE);
				return false;
			}
		} else {
			// si mesa es null, no pudo transformarlo a mesa. Sigo editando
			return false;
		}
	} //agregarMesa

	
	/** si no hay errores en los campos, modifica un mesa con dichos campos */
	private void modificarMesa() {
		Mesa mesa = campos2Mesa();
		if ( mesa != null ) {
			if ( mesaData.modificarMesa(mesa) )  {// si pudo  modificar al mesa
				cargarListaMesas();
				cargarTabla();
			} else 
				JOptionPane.showMessageDialog(this, "Debe completar correctamente todos los datos de la mesa para modificarla", "No se puede agregar", JOptionPane.ERROR_MESSAGE);			
		} else {
			// si mesa es null, no pudo transformarlo a mesa. Sigo editando
		}	
	} //modificarMesa
	
	
	
	/**
	 * Busca al mesa por id, por capacidad, por estado o por idMesero (o por 
	 * combinación de dichos campos). 
	 * El criterio para usar un campo en la búsqueda es que no esté en blanco. 
	 * Es decir, si tiene datos, se buscará por ese dato. Por ejemplo, si puso 
	 * el id, buscará por id. Si puso el cantidad, buscará por cantidad. 
	 * Si puso el cantidad y idMesero, buscara por cantidad and idMesero.
	 * 
	 * @return devuelve true sio pudo usar algún criterio de búsqueda
	 */
	private boolean buscarMesa(){ 
		// cargo los campos de texto id, dni, apellido y nombre para buscar por esos criterior
		int idMesa, capacidad;
		Mesa.EstadoMesa estado;
		int idMesero;
		
		//idMesa
		try {
			if (txtIdMesa.getText().isEmpty()) // si está vacío no se usa para buscar
				idMesa = -1;
			else
				idMesa = Integer.valueOf(txtIdMesa.getText()); //no vacío, participa del criterio de búsqueda
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this, "El Id debe ser un número válido", "Id no válido", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		//capacidad
		try {
			if (txtCapacidad.getText().isEmpty()) // si está vacío no se usa para buscar
				capacidad = -1;
			else
				capacidad = Integer.valueOf(txtCapacidad.getText()); // no vacío, participa del criterio de búsqueda
				
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this, "La capacidad debe ser un número válido", "Capacidad no válida", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
		//estado
		if (rbEstadoLibre.isSelected())
			estado = Mesa.EstadoMesa.LIBRE;
		else if (rbEstadoOcupada.isSelected())
			estado = Mesa.EstadoMesa.OCUPADA;
		else if (rbEstadoAtendida.isSelected())
			estado = Mesa.EstadoMesa.ATENDIDA;
		else 
			estado = null;
		
		//idMesero
		Servicio mesero = (Servicio) cbIdNombreMesero.getSelectedItem();
		idMesero = (mesero==null) ? -1 : mesero.getIdServicio();
		
		//testeo que hay al menos un criterio de búsqueda
		if ( idMesa==-1 && capacidad==-1 && estado==null && idMesero==-1  )   {
			JOptionPane.showMessageDialog(this, "Debe ingresar algún criterio para buscar", "Ningun criterio de búsqueda", JOptionPane.ERROR_MESSAGE);
			return false;
		} else { //todo Ok. Buscar por alguno de los criterior de búsqueda
			filtro.idMesa = idMesa;
			filtro.capacidad = capacidad;
			filtro.estado = estado;
			filtro.idMesero = idMesero;
			filtro.estoyFiltrando = true;
			cargarListaMesas();
			cargarTabla();
			return true; // pudo buscar
		}
	} //buscarMesa
	

	
	
	
	/** deshabilito todos los botones y tabla, habilito guardar/cancelar */
	private void habilitoParaBuscar(){ 
		habilitoParaEditar();
		txtIdMesa.setEditable(true);
	} //habilitoParaBuscar

	
		
	
	/** deshabilito todos los botones y tabla, habilito guardar/cancelar */
	private void habilitoParaEditar(){ 
		// deshabilito todos los botones (menos salir)
		btnAgregar.setEnabled(false);
		btnModificar.setEnabled(false); //deshabilito botón modificar
		btnEliminar.setEnabled(false);  //deshabilito botón eliminar
		btnBuscar.setEnabled(false);
		cboxOrden.setEnabled(false);
		
		//Deshabilito la Tabla para que no pueda hacer click
		tablaMesas.setEnabled(false);
		
		//Habilito los botones guardar y cancelar
		btnGuardar.setEnabled(true); // este botón es el que realmente se encargará de agregegar el mesa
		btnCancelar.setEnabled(true);
		
		//Habilito los campos para poder editar
		txtCapacidad.setEditable(true);
		rbEstadoLibre.setEnabled(true);
		rbEstadoOcupada.setEnabled(true);
		rbEstadoAtendida.setEnabled(true);
		cbIdNombreMesero.setEnabled(true);
	} //habilitoParaEditar

	
	
	
	/** habilito todos los botones y tabla, deshabilito guardar/cancelar y modificar */
	private void deshabilitoParaEditar(){ 
		limpiarCampos(); //Pongo todos los campos de texto en blanco
		// habilito todos los botones (menos salir)
		btnAgregar.setEnabled(true);
		btnBuscar.setEnabled(true);
		cboxOrden.setEnabled(true);
		
		//sigo deshabilitando los botones modificar y eliminar porque no hay una fila seleccionada.
		btnModificar.setEnabled(false); //deshabilito botón modificar
		btnEliminar.setEnabled(false);  //deshabilito botón eliminar
		
		//Habilito la Tabla para que pueda hacer click
		tablaMesas.setEnabled(true);
		
		//Deshabilito el boton guardar 
		btnGuardar.setEnabled(false);  
		botonGuardarComoGuardar(); //por si estaba buscando cambio icono y texto del btnGuardar a "Guardar"
		
		//deshabilito el boton cancelar
		btnCancelar.setEnabled(false);

		//deshabilito los campos para poder que no pueda editar
		txtIdMesa.setEditable(false);
		txtCapacidad.setEditable(false);
		rbEstadoLibre.setEnabled(false);
		rbEstadoOcupada.setEnabled(false);
		rbEstadoAtendida.setEnabled(false);
		cbIdNombreMesero.setEnabled(false);
	} //deshabilitoParaEditar

	
	
	
	
	/** pongo los campos txtfield en blanco y deselecciono la fila de tabla */
	private void limpiarCampos(){
		//pongo los campos en blanco
		txtIdMesa.setText("");
		txtCapacidad.setText("");
		//rbEstadoLibre.setSelected(false);
		//rbEstadoOcupada.setSelected(false);
		//rbEstadoAtendida.setSelected(false);
		btngrpEstado.clearSelection();
		cbIdNombreMesero.setSelectedIndex(-1);
		
		if (tablaMesas.getRowCount() > 0) 
			tablaMesas.removeRowSelectionInterval(0, tablaMesas.getRowCount()-1); //des-selecciono las filas de la tabla
	} // limpiarCampos




	/**
	 * cargo los datos de la fila indicada de la tabla a los campos de texto de la pantalla 
	 * @param numfila el número de fila a cargar a los campos
	 */
	private void filaTabla2Campos(int numfila){
		txtIdMesa.setText(tablaMesas.getValueAt(numfila, 0)+"");
		txtCapacidad.setText(tablaMesas.getValueAt(numfila, 1)+"");
		
		if ((Mesa.EstadoMesa)tablaMesas.getValueAt(numfila, 2) == Mesa.EstadoMesa.LIBRE)
			rbEstadoLibre.setSelected(true);
		else if ((Mesa.EstadoMesa)tablaMesas.getValueAt(numfila, 2) == Mesa.EstadoMesa.OCUPADA)
			rbEstadoOcupada.setSelected(true);
		else if ((Mesa.EstadoMesa)tablaMesas.getValueAt(numfila, 2) == Mesa.EstadoMesa.ATENDIDA)
			rbEstadoAtendida.setSelected(true);
		
		cbIdNombreMesero.setSelectedItem(tablaMesas.getValueAt(numfila, 3));
	} //filaTabla2Campos


	
	
	/**
	 * Cargo los campos de texto de la pantalla a un objeto tipo Mesa
	 * @return El Mesa devuelto. Si hay algún error, devuelve null
	 */
	private Mesa campos2Mesa(){ 
		int idMesa, capacidad;
		Mesa.EstadoMesa estado;
		int idMesero;
		
		//idMesa
		try {
			if (txtIdMesa.getText().isEmpty()) // en el alta será un string vacío
				idMesa = -1;
			else
				idMesa = Integer.valueOf(txtIdMesa.getText()); // obtengo el identificador el mesa
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this, "El IdMesa debe ser un número válido", "IdMesa no válido", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		
		//capacidad
		try {
			capacidad = Integer.valueOf(txtCapacidad.getText());
				
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this, "La capacidad debe ser un número válido", "Capacidad no válida", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		
		//estado
		if (rbEstadoLibre.isSelected())
			estado = Mesa.EstadoMesa.LIBRE;
		else if (rbEstadoOcupada.isSelected())
			estado = Mesa.EstadoMesa.OCUPADA;
		else if (rbEstadoAtendida.isSelected())
			estado = Mesa.EstadoMesa.ATENDIDA;
		else 
			estado = null;
		
		//idMesero
		idMesero = (cbIdNombreMesero.getSelectedItem()==null) ? 0 : ((Servicio) cbIdNombreMesero.getSelectedItem()).getIdServicio();
		
		return new Mesa(idMesa, capacidad, estado, idMesero);
	} // campos2Mesa
	
	
	
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

	
	/** 
	 * cambia titulo y color de panel de tabla para reflejar que está filtrada.
	 * Habilita btnResetearFiltro
	*/
	private void setearFiltro(){
			//cambio el titulo de la tabla y color panel de tabla para que muestre que está filtrado
			lblTituloTabla.setText("Listado de mesas filtradas por búsqueda");
			panelTabla.setBackground(new Color(255, 51, 51));
			btnResetearFiltro.setEnabled(true);
			filtro.estoyFiltrando = true;
	} //setearFiltro
	
	
	/** 
	 * Restaur titulo y color de panel de tabla para reflejar que ya no está filtrada.
	 * Deshabilita btnResetearFiltro
	*/
	private void resetearFiltro(){
			//cambio el titulo de la tabla y color panel de tabla para que muestre que no está filtrado
			//cambio el titulo de la tabla y color panel de tabla para que muestre que está filtrado
			lblTituloTabla.setText("Listado de mesas");
			panelTabla.setBackground(new Color(153, 153, 255));
			btnResetearFiltro.setEnabled(false);
			filtro.estoyFiltrando = false;
	} //setearFiltro
	
	
	
	
	

	
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
        txtIdMesa = new javax.swing.JTextField();
        cbIdNombreMesero = new javax.swing.JComboBox<>();
        btnGuardar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        txtCapacidad = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        rbEstadoLibre = new javax.swing.JRadioButton();
        rbEstadoOcupada = new javax.swing.JRadioButton();
        rbEstadoAtendida = new javax.swing.JRadioButton();
        panelTabla = new javax.swing.JPanel();
        lblTituloTabla = new javax.swing.JLabel();
        btnResetearFiltro = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaMesas = new javax.swing.JTable();
        botonera = new javax.swing.JPanel();
        btnAgregar = new javax.swing.JButton();
        btnModificar = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        btnBuscar = new javax.swing.JButton();
        btnSalir = new javax.swing.JButton();
        cboxOrden = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();

        panelCamposMesa.setBackground(new java.awt.Color(153, 153, 255));
        panelCamposMesa.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N

        txtIdMesa.setEditable(false);
        txtIdMesa.setBorder(javax.swing.BorderFactory.createTitledBorder("Id Mesa"));

        cbIdNombreMesero.setBorder(javax.swing.BorderFactory.createTitledBorder("Mesero"));
        cbIdNombreMesero.setEnabled(false);

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
        jLabel7.setText("Gestión de Mesas");

        txtCapacidad.setEditable(false);
        txtCapacidad.setBorder(javax.swing.BorderFactory.createTitledBorder("Capacidad"));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Estado"));

        btngrpEstado.add(rbEstadoLibre);
        rbEstadoLibre.setText("Libre");
        rbEstadoLibre.setEnabled(false);

        btngrpEstado.add(rbEstadoOcupada);
        rbEstadoOcupada.setText("Ocupada");
        rbEstadoOcupada.setEnabled(false);

        btngrpEstado.add(rbEstadoAtendida);
        rbEstadoAtendida.setText("Atendida");
        rbEstadoAtendida.setEnabled(false);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(rbEstadoLibre)
                .addGap(18, 18, 18)
                .addComponent(rbEstadoOcupada)
                .addGap(18, 18, 18)
                .addComponent(rbEstadoAtendida)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(rbEstadoLibre, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(rbEstadoOcupada)
                .addComponent(rbEstadoAtendida))
        );

        javax.swing.GroupLayout panelCamposMesaLayout = new javax.swing.GroupLayout(panelCamposMesa);
        panelCamposMesa.setLayout(panelCamposMesaLayout);
        panelCamposMesaLayout.setHorizontalGroup(
            panelCamposMesaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCamposMesaLayout.createSequentialGroup()
                .addGroup(panelCamposMesaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelCamposMesaLayout.createSequentialGroup()
                        .addGap(77, 77, 77)
                        .addComponent(jLabel7))
                    .addGroup(panelCamposMesaLayout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addGroup(panelCamposMesaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelCamposMesaLayout.createSequentialGroup()
                                .addComponent(btnGuardar)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnCancelar))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelCamposMesaLayout.createSequentialGroup()
                                .addComponent(txtIdMesa, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txtCapacidad, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cbIdNombreMesero, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(24, Short.MAX_VALUE))
        );
        panelCamposMesaLayout.setVerticalGroup(
            panelCamposMesaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCamposMesaLayout.createSequentialGroup()
                .addComponent(jLabel7)
                .addGap(35, 35, 35)
                .addGroup(panelCamposMesaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtIdMesa, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCapacidad, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cbIdNombreMesero, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(panelCamposMesaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnGuardar)
                    .addComponent(btnCancelar))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelTabla.setBackground(new java.awt.Color(153, 153, 255));

        lblTituloTabla.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lblTituloTabla.setText("Listado de Mesas");
        lblTituloTabla.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        btnResetearFiltro.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnResetearFiltro.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/restart16x16.png"))); // NOI18N
        btnResetearFiltro.setText("Resetear filtro");
        btnResetearFiltro.setEnabled(false);
        btnResetearFiltro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetearFiltroActionPerformed(evt);
            }
        });

        tablaMesas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id", "Capacidad", "Estado", "Mesero"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tablaMesas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaMesasMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tablaMesas);
        if (tablaMesas.getColumnModel().getColumnCount() > 0) {
            tablaMesas.getColumnModel().getColumn(0).setResizable(false);
            tablaMesas.getColumnModel().getColumn(0).setPreferredWidth(4);
            tablaMesas.getColumnModel().getColumn(1).setResizable(false);
            tablaMesas.getColumnModel().getColumn(1).setPreferredWidth(4);
            tablaMesas.getColumnModel().getColumn(2).setResizable(false);
            tablaMesas.getColumnModel().getColumn(2).setPreferredWidth(4);
            tablaMesas.getColumnModel().getColumn(3).setPreferredWidth(75);
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
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnResetearFiltro))
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );
        panelTablaLayout.setVerticalGroup(
            panelTablaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTablaLayout.createSequentialGroup()
                .addGroup(panelTablaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTituloTabla)
                    .addComponent(btnResetearFiltro))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        botonera.setBackground(new java.awt.Color(153, 153, 255));

        btnAgregar.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnAgregar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/mesa3_32x32.png"))); // NOI18N
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

        btnBuscar.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnBuscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/buscar1_32x32.png"))); // NOI18N
        btnBuscar.setText("Buscar");
        btnBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarActionPerformed(evt);
            }
        });

        btnSalir.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/salida1_32x32.png"))); // NOI18N
        btnSalir.setText("Salir");
        btnSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalirActionPerformed(evt);
            }
        });

        cboxOrden.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "por IdMesa", "por Capacidad", "por Estado", "por Mesero" }));
        cboxOrden.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboxOrdenActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel6.setText("Ordenado");

        javax.swing.GroupLayout botoneraLayout = new javax.swing.GroupLayout(botonera);
        botonera.setLayout(botoneraLayout);
        botoneraLayout.setHorizontalGroup(
            botoneraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(botoneraLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(btnAgregar)
                .addGap(18, 18, 18)
                .addComponent(btnModificar)
                .addGap(18, 18, 18)
                .addComponent(btnEliminar)
                .addGap(18, 18, 18)
                .addComponent(btnBuscar)
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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, botoneraLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(botoneraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAgregar)
                    .addComponent(btnModificar)
                    .addComponent(btnEliminar)
                    .addComponent(btnBuscar)
                    .addComponent(btnSalir)
                    .addComponent(cboxOrden, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
            .addGroup(botoneraLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panelCamposMesa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelTabla, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(botonera, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(panelCamposMesa, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelTabla, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(botonera, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

	
	
//================================================================================
//================================================================================
	
	/** permite editar en los campos, habilita boton de guardar/cancelar y deshabilita otros botones.
	    El alta verdadera lo realiza el botón de guardar (si no eligió cancelar) */
    private void btnAgregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarActionPerformed
        tipoEdicion = TipoEdicion.AGREGAR;  //para que el boton guardar sepa que estoy queriendo agregar una mesa
        limpiarCampos(); //Pongo todos los campos de texto en blanco
        habilitoParaEditar();
    }//GEN-LAST:event_btnAgregarActionPerformed

	
	/** 
	 * Permite editar en los campos, habilita boton de guardar/cancelar y deshabilita otros botones.
	 * La modificación verdadera lo realiza el botón de guardar (si no eligió cancelar)
	 */ 
    private void btnModificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModificarActionPerformed
        tipoEdicion = TipoEdicion.MODIFICAR; //para que el boton guardar sepa que estoy queriendo modificar un mesa
        habilitoParaEditar();
    }//GEN-LAST:event_btnModificarActionPerformed

	
	/** 
	 * Elimina la mesa seleccionado de la tabla. 
	 * Como no queda ninguna seleccionado, deshabilito botones btnModificar y btnEliminar
	 */
    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
        if ( eliminarMesa() ) { // si pudo eliminar
            limpiarCampos(); //Pongo todos los campos de texto en blanco
            btnModificar.setEnabled(false); //deshabilito botón modificar
            btnEliminar.setEnabled(false);  //deshabilito botón eliminar
            cargarListaMesas();
            cargarTabla();
        }
    }//GEN-LAST:event_btnEliminarActionPerformed

	
	
	/**
	 * Permite editar en los campos, cambia el botón guardar a buscar, 
	 * habilita boton de guardar/cancelar y deshabilita otros botones.
	 * La búsqueda verdadera lo realiza el botón de guardar (si no eligió cancelar)
	 */
    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarActionPerformed
        tipoEdicion = TipoEdicion.BUSCAR; //para que el boton guardar sepa que estoy queriendo buscar un mesa
        limpiarCampos();
        botonGuardarComoBuscar(); //cambio icono y texto del btnGuardar a "Buscar"
        habilitoParaBuscar();
    }//GEN-LAST:event_btnBuscarActionPerformed

	
	
	/** Cierra la ventana (termina CrudMesas */
    private void btnSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalirActionPerformed
        dispose();//cierra la ventana
    }//GEN-LAST:event_btnSalirActionPerformed

	
	
/**
 * Permite ordenar la lista de mesas por el criterio de este combo box
 * @param evt 
 */	
    private void cboxOrdenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboxOrdenActionPerformed
        if (cboxOrden.getSelectedIndex() == 0)
			ordenacion = OrdenacionMesa.PORIDMESA;
        else if (cboxOrden.getSelectedIndex() == 1)
        ordenacion = OrdenacionMesa.PORCAPACIDAD;
		else if (cboxOrden.getSelectedIndex() == 2)
			ordenacion = OrdenacionMesa.PORESTADO;
		else if (cboxOrden.getSelectedIndex() == 3)
			ordenacion = OrdenacionMesa.PORMESERO;
        else // por las dudas que no eligio uno correcto
        ordenacion = OrdenacionMesa.PORIDMESA;

        cargarListaMesas();
        cargarTabla();
        limpiarCampos();
        botonGuardarComoGuardar();
        deshabilitoParaEditar();
    }//GEN-LAST:event_cboxOrdenActionPerformed

	
	
	
	
	
	/** con los campos de texto de la pantalla hace un agregarMesa, modificarMesa o buscarMesa
	    en base a la variable tipoEdicion, ya sea AGREGAR, MODIFICAR o BUSCAR */
    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        if ( tipoEdicion == TipoEdicion.AGREGAR ){ //agregar el mesa
            agregarMesa();
            resetearFiltro();
        } else if ( tipoEdicion == TipoEdicion.MODIFICAR ) { // modificar el mesa
            modificarMesa();
            resetearFiltro();
        } else { // tipoEdicion = BUSCAR: quiere buscar un mesa
            buscarMesa();
            setearFiltro();
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

	
	
	/** 
	 * Restaura la tabla a la lista total, pone los campos en blanco, 
	 * restaura el color de fondo del panel y deshabilita btnResetearFiltro
	 * @param evt 
	 */
    private void btnResetearFiltroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetearFiltroActionPerformed
        resetearFiltro();
        cargarListaMesas();
        cargarTabla();
        limpiarCampos();
        botonGuardarComoGuardar();//por si estaba buscando cambio icono y texto del btnGuardar a "Guardar"
        deshabilitoParaEditar();
    }//GEN-LAST:event_btnResetearFiltroActionPerformed

	
	
	/** al hacer clik en una fila de la tabla, queda seleccionado una mesa.
	 * Entonces habilita los botones de eliminar y modificar
	 * @param evt 
	 */
    private void tablaMesasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaMesasMouseClicked
       //tabla.addRowSelectionInterval(filaTabla, filaTabla); //selecciono esa fila de la tabla
        if (tablaMesas.getSelectedRow() != -1){ // si hay alguna fila seleccionada
		}
		int numfila = tablaMesas.getSelectedRow();
		if (numfila != -1) {			
			btnEliminar.setEnabled(true); // habilito el botón de eliminar
			btnModificar.setEnabled(true); // habilito el botón de modificar
			
			filaTabla2Campos(numfila); // cargo los campos de texto de la pantalla con datos de la fila seccionada de la tabla
		}  
    }//GEN-LAST:event_tablaMesasMouseClicked


//================================================================================
//================================================================================
	
		
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel botonera;
    private javax.swing.JButton btnAgregar;
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnModificar;
    private javax.swing.JButton btnResetearFiltro;
    private javax.swing.JButton btnSalir;
    private javax.swing.ButtonGroup btngrpEstado;
    private javax.swing.JComboBox<Servicio> cbIdNombreMesero;
    private javax.swing.JComboBox<String> cboxOrden;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblTituloTabla;
    private javax.swing.JPanel panelCamposMesa;
    private javax.swing.JPanel panelTabla;
    private javax.swing.JRadioButton rbEstadoAtendida;
    private javax.swing.JRadioButton rbEstadoLibre;
    private javax.swing.JRadioButton rbEstadoOcupada;
    private javax.swing.JTable tablaMesas;
    private javax.swing.JTextField txtCapacidad;
    private javax.swing.JTextField txtIdMesa;
    // End of variables declaration//GEN-END:variables
} // CrudMesas



//================================================================================
//================================================================================
	


/**
 * Es una clase para agrupar y almacenar los datos con los que se filtra una búsqueda
 * @author John David Molina Velarde
 */
class FiltroMesas{
	int idMesa;
	int capacidad;
	Mesa.EstadoMesa estado;
	int idMesero;
	boolean estoyFiltrando;

	public FiltroMesas() { // constructor
		idMesa = -1;
		capacidad = -1;
		estado = null;
		idMesero = -1;
		estoyFiltrando = false;
	} // constructor FiltroMesas
} //FiltroMesas