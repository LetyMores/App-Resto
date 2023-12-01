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

import entidades.Servicio;
import accesoadatos.ServicioData;
import accesoadatos.ServicioData.OrdenacionServicio;
import entidades.Mesa;
import accesoadatos.MesaData;
import utiles.Utils;
import java.awt.Color;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *							 
 * @author John David Molina Velarde, Leticia Mores, Enrique Germán Martínez, Carlos Eduardo Beltrán
 */
public class CrudServicios extends javax.swing.JInternalFrame {
	private DefaultTableModel modeloTabla, modeloTablaMesasAsignadas, modeloTablaMesasNoAsignadas;
	private ServicioData servicioData;
	private MesaData mesaData;
	private List<Mesa> listaMesas;
	private LinkedHashMap<Integer,Servicio> mapaServicios;
	private enum TipoEdicion {AGREGAR, MODIFICAR, BUSCAR};
	private TipoEdicion tipoEdicion = TipoEdicion.AGREGAR; //para que el boton guardar sepa que estoy queriendo hacer:
														   // Si con los campos voy a agregar, modificar o buscar una servicio
	private OrdenacionServicio ordenacion = OrdenacionServicio.PORIDSERVICIO; // defino el tipo de orden por defecto 
	private FiltroServicios filtro = new FiltroServicios();  //el filtro de búsqueda
	
	
	public CrudServicios() {
		initComponents();
		cargaCbTipo(); // carga el combo box de Tipo
		servicioData = new ServicioData(); 
		mesaData = new MesaData();
		modeloTabla = (DefaultTableModel) tablaServicios.getModel();
		modeloTablaMesasAsignadas = (DefaultTableModel) tablaMesasAsignadas.getModel();
		modeloTablaMesasNoAsignadas = (DefaultTableModel) tablaMesasNoAsignadas.getModel();
		cargarMapaServicios(); //carga la base de datos
		cargarListaMesas();
		cargarTabla(); // cargo la tabla con las servicios
		cargarTablaMesas(-1); // cargo la tabla con las mesas asignadas y no asignadas al mesero -1 (nadie)
	}

	/**
	 * Carga la lista de mesas de la tabla de Mesas
	 */
	private void cargarListaMesas(){
		// cargo la lista de mesas
		listaMesas = mesaData.getListaMesas();
	}
	
	/**
	 * carga el combo box cbTipo con los valores correctos ADMINISTRACION, SERVICIO, MESERO, RECEPCION
	 */
	private void cargaCbTipo(){ 
		cbTipo.addItem(Servicio.TipoServicio.ADMINISTRACION);
		cbTipo.addItem(Servicio.TipoServicio.SERVICIO);
		cbTipo.addItem(Servicio.TipoServicio.MESERO);
		cbTipo.addItem(Servicio.TipoServicio.RECEPCION);
	} //cargaCbTipo
	
	
	
	/** carga la lista de servicios de la BD */
	private void cargarMapaServicios(){ 
		List<Servicio> listaServicios;
		if (filtro.estoyFiltrando) 
			listaServicios = servicioData.getListaServiciosXCriterioDeBusqueda(filtro.idServicio, filtro.nombreServicio, "", -1, filtro.tipo, ordenacion);
		else
			listaServicios = servicioData.getListaServicios(ordenacion);
		
		mapaServicios = new LinkedHashMap(); 
		listaServicios.stream().forEach( servicio -> mapaServicios.put(servicio.getIdServicio(), servicio) ); // notación labmda con streams
	}
	
	
	/** carga servicios de la lista a la tabla */
	private void cargarTabla(){ 
		//borro las filas de la tabla
		for (int fila = modeloTabla.getRowCount() -  1; fila >= 0; fila--)
			modeloTabla.removeRow(fila);
		
		//cargo los servicios de listaServicios a la tabla
		for (Servicio servicio : mapaServicios.values()) {
			modeloTabla.addRow(new Object[] {
				servicio.getIdServicio(),
				servicio.getNombreServicio(),
				servicio.getHost(),
				servicio.getPuerto(),
				servicio.getTipo()
			} );
		}
		
		//como no hay fila seleccionada, deshabilito el botón Eliminar y Modificar
		if (tablaServicios.getSelectedRow() == -1) {// si no hay alguna fila seleccionada
			btnEliminar.setEnabled(false); // deshabilito el botón de eliminar
			btnModificar.setEnabled(false); // deshabilito el botón de Modificar
		}
	} //cargarTabla
	
	
	/**
	 * Carga la tabla de mesas asignadas al mesero y las no asignadas a él.
	 * @param idMesero el mesero cuyas mesas se cargarán.
	 */
	private void cargarTablaMesas(int idMesero){ 
		//borro las filas de la tabla mesas asignadas
		for (int fila = modeloTablaMesasAsignadas.getRowCount() -  1; fila >= 0; fila--)
			modeloTablaMesasAsignadas.removeRow(fila);
		
		//borro las filas de la tabla mesas no asignadas
		for (int fila = modeloTablaMesasNoAsignadas.getRowCount() -  1; fila >= 0; fila--)
			modeloTablaMesasNoAsignadas.removeRow(fila);
		
		//cargo los servicios de listaMesas a la tabla de mesas
		for (Mesa mesa : listaMesas) {
			if (mesa.getIdMesero() == idMesero) 
				modeloTablaMesasAsignadas.addRow(new Object[] {
					mesa.getIdMesa()
				} );
			else {
				modeloTablaMesasNoAsignadas.addRow(new Object[] {
					mesa.getIdMesa(), 
					mesa.getIdMesero(),
					( (mesa.getIdMesero() <= 0) ? "" : mapaServicios.get(mesa.getIdMesero()).getNombreServicio() )
				} );
			}
		}
		
		//como no hay fila seleccionada en la tablaMesasAsignadas, deshabilito el botón Desasignar
		if (tablaMesasAsignadas.getSelectedRow() == -1) // si no hay alguna fila seleccionada
			btnDesasignarMesa.setEnabled(false); // deshabilito el botón de Desasignar
		else //hay una fila seleccionada
			btnDesasignarMesa.setEnabled(true); // habilito el botón de Desasignar
        
		//como no hay fila seleccionada tablaMesasNoAsignadas, deshabilito el botón Asignar
		if (tablaMesasNoAsignadas.getSelectedRow() == -1) // si no hay alguna fila seleccionada
			btnAsignarMesa.setEnabled(false); // deshabilito el botón de Asignar Mesa
		else //hay una fila seleccionada
			btnAsignarMesa.setEnabled(true); // deshabilito el botón de Asignar mesa
	} //cargarTablaMesas
	
	
	
	/** 
	 * Elimina al servicio seleccionado de la lista y la bd. 
	 * @return Devuelve true si pudo eliminarlo
	 */
	private boolean eliminarServicio(){ 
		int fila = tablaServicios.getSelectedRow();
        if (fila != -1) { // Si hay alguna fila seleccionada
			int idServicio = Integer.parseInt(txtIdServicio.getText());
			if (servicioData.bajaServicio(idServicio)){ 
				mapaServicios.remove(idServicio);
				return true;
			} else
				return false;
            //tabla.removeRowSelectionInterval(0, tabla.getRowCount()-1); //des-selecciono las filas de la tabla
        } else {
			JOptionPane.showMessageDialog(this, "Debe seleccionar un servicio para eliminar", "Ningún servicio seleccionado", JOptionPane.ERROR_MESSAGE);
			return false;
		}
	} //eliminarServicio
	
	
	/**
	 * si no hay errores en los campos, agrega un servicio con dichos campos. 
	 * @return Devuelve true si pudo agregarlo
	 */
	private boolean agregarServicio(){
		Servicio servicio = campos2Servicio();
		if ( servicio != null ) {
			if ( servicioData.altaServicio(servicio) ) {// si pudo dar de alta al servicio
				cargarMapaServicios();
				cargarTabla();
				cargarTablaMesas(-1);
				return true;
			} else {
				JOptionPane.showMessageDialog(this, "Debe completar correctamente todos los datos del servicio para agregarlo", "No se puede agregar", JOptionPane.ERROR_MESSAGE);
				return false;
			}
		} else {
			// si servicio es null, no pudo transformarlo a servicio. Sigo editando
			return false;
		}
	} //agregarServicio

	
	/** si no hay errores en los campos, modifica un servicio con dichos campos */
	private void modificarServicio() {
		Servicio servicio = campos2Servicio();
		if ( servicio != null ) {
			if ( servicioData.modificarServicio(servicio) )  {// si pudo  modificar al servicio
				cargarMapaServicios();
				cargarTabla();
			} else 
				JOptionPane.showMessageDialog(this, "Debe completar correctamente todos los datos del servicio para modificarlo", "No se puede modificar", JOptionPane.ERROR_MESSAGE);			
		} else {
			// si servicio es null, no pudo transformarlo a servicio. Sigo editando
		}	
	} //modificarServicio
	
	
	
	/**
	 * Busca al servicio por id, por capacidad, por estado o por idMesero (o por 
	 * combinación de dichos campos). 
	 * El criterio para usar un campo en la búsqueda es que no esté en blanco. 
	 * Es decir, si tiene datos, se buscará por ese dato. Por ejemplo, si puso 
	 * el id, buscará por id. Si puso el cantidad, buscará por cantidad. 
	 * Si puso el cantidad y idMesero, buscara por cantidad and idMesero.
	 * 
	 * @return devuelve true sio pudo usar algún criterio de búsqueda
	 */
	private boolean buscarServicio(){ 
		// cargo los campos de texto id, nombre, tipo para buscar por esos criterior
		int idServicio;
		String nombreServicio;
		Servicio.TipoServicio tipo;
		
		//idServicio
		try {
			if (txtIdServicio.getText().isEmpty()) // si está vacío no se usa para buscar
				idServicio = -1;
			else
				idServicio = Integer.valueOf(txtIdServicio.getText()); //no vacío, participa del criterio de búsqueda
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this, "El Id debe ser un número válido", "Id no válido", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
		//nombreServicio
		if (txtNombre.getText().isEmpty()) // si está vacío no se usa para buscar
			nombreServicio = "";
		else
			nombreServicio = txtNombre.getText(); // no vacío, participa del criterio de búsqueda
		
		//tipo
		tipo = (Servicio.TipoServicio)cbTipo.getSelectedItem();
		
		//testeo que hay al menos un criterio de búsqueda
		if ( idServicio==-1 && nombreServicio.isEmpty() && tipo==null )   {
			JOptionPane.showMessageDialog(this, "Debe ingresar algún criterio para buscar", "Ningun criterio de búsqueda", JOptionPane.ERROR_MESSAGE);
			return false;
		} else { //todo Ok. Buscar por alguno de los criterior de búsqueda
			filtro.idServicio = idServicio;
			filtro.nombreServicio = nombreServicio;
			filtro.tipo = tipo;
			filtro.estoyFiltrando = true;
			cargarMapaServicios();
			cargarTabla();
			return true; // pudo buscar
		}
	} //buscarServicio
	

	
	
	
	/** deshabilito todos los botones y tabla, habilito guardar/cancelar */
	private void habilitoParaBuscar(){ 
		habilitoParaEditar();
		txtIdServicio.setEditable(true);
		txtNombre.setEditable(true);
		txtHost.setEditable(false);
		txtPuerto.setEditable(false);
		cbTipo.setEditable(true);
		cbTipo.setEnabled(true);
		pwdClave.setEditable(false);
	} //habilitoParaBuscar

	
		
	
	/** deshabilito todos los botones y tabla, habilito guardar/cancelar */
	private void habilitoParaEditar(){ 
		// deshabilito todos los botones (menos salir)
		btnAgregar.setEnabled(false);
		btnModificar.setEnabled(false); //deshabilito botón modificar
		btnEliminar.setEnabled(false);  //deshabilito botón eliminar
		btnBuscar.setEnabled(false);
		cboxOrden.setEnabled(false);
		btnAsignarMesa.setEnabled(false);
		btnDesasignarMesa.setEnabled(false);
		
		//Deshabilito la Tabla para que no pueda hacer click
		tablaServicios.setEnabled(false);
		tablaMesasAsignadas.setEnabled(false);
		tablaMesasNoAsignadas.setEnabled(false);
		if (tablaMesasAsignadas.getRowCount() > 0)
			tablaMesasAsignadas.removeRowSelectionInterval(0, tablaMesasAsignadas.getRowCount()-1); //des-selecciono las filas de la tabla
		if (tablaMesasNoAsignadas.getRowCount() > 0)
			tablaMesasNoAsignadas.removeRowSelectionInterval(0, tablaMesasNoAsignadas.getRowCount()-1); //des-selecciono las filas de la tabla
		
		//Habilito los botones guardar y cancelar
		btnGuardar.setEnabled(true); // este botón es el que realmente se encargará de agregegar el servicio
		btnCancelar.setEnabled(true);
		
		//Habilito los campos para poder editar
		txtNombre.setEditable(true);
		txtHost.setEditable(true);
		txtPuerto.setEditable(true);
		cbTipo.setEditable(true);
		cbTipo.setEnabled(true);
		pwdClave.setEditable(true);
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
		tablaServicios.setEnabled(true);
		
		//Deshabilito el boton guardar 
		btnGuardar.setEnabled(false);  
		botonGuardarComoGuardar(); //por si estaba buscando cambio icono y texto del btnGuardar a "Guardar"
		
		//deshabilito el boton cancelar
		btnCancelar.setEnabled(false);

		//deshabilito los campos para poder que no pueda editar
		txtIdServicio.setEditable(false);
		txtNombre.setEditable(false);
		txtHost.setEditable(false);
		txtPuerto.setEditable(false);
		cbTipo.setEditable(false);
		cbTipo.setEnabled(false);
		pwdClave.setEditable(false);
	} //deshabilitoParaEditar

	
	
	
	
	/** pongo los campos txtfield en blanco y deselecciono la fila de tabla */
	private void limpiarCampos(){
		//pongo los campos en blanco
		txtIdServicio.setText("");
		txtNombre.setText("");
		txtHost.setText("");
		txtPuerto.setText("");
		cbTipo.setSelectedIndex(-1); //lo mismo se logra con cbTipo.setSelectedItem(null);
		pwdClave.setText("");
		
		if (tablaServicios.getRowCount() > 0) 
			tablaServicios.removeRowSelectionInterval(0, tablaServicios.getRowCount()-1); //des-selecciono las filas de la tabla
		cargarTablaMesas(0);
		if (tablaMesasAsignadas.getRowCount() > 0)
			tablaMesasAsignadas.removeRowSelectionInterval(0, tablaMesasAsignadas.getRowCount()-1); //des-selecciono las filas de la tabla
		if (tablaMesasNoAsignadas.getRowCount() > 0)
			tablaMesasNoAsignadas.removeRowSelectionInterval(0, tablaMesasNoAsignadas.getRowCount()-1); //des-selecciono las filas de la tabla
	} // limpiarCampos




	/**
	 * cargo los datos de la fila indicada de la tabla a los campos de texto de la pantalla 
	 * @param numfila el número de fila a cargar a los campos
	 */
	private void filaTabla2Campos(int numfila){
		txtIdServicio.setText(tablaServicios.getValueAt(numfila, 0)+"");
		txtNombre.setText(tablaServicios.getValueAt(numfila, 1)+"");
		txtHost.setText(tablaServicios.getValueAt(numfila, 2)+"");
		txtPuerto.setText(tablaServicios.getValueAt(numfila,3)+"");
		cbTipo.setSelectedItem(tablaServicios.getValueAt(numfila,4) );
		pwdClave.setText( mapaServicios.get(Integer.valueOf( txtIdServicio.getText())).getClave() );
	} //filaTabla2Campos


	
	
	/**
	 * Cargo los campos de texto de la pantalla a un objeto tipo Servicio
	 * @return El Servicio devuelto. Si hay algún error, devuelve null
	 */
	private Servicio campos2Servicio(){ 
		int idServicio;
		String nombre, host;
		int puerto;
		Servicio.TipoServicio tipo;
		String clave;
		
		//idServicio
		try {
			if (txtIdServicio.getText().isEmpty()) // en el alta será un string vacío
				idServicio = -1;
			else
				idServicio = Integer.valueOf(txtIdServicio.getText()); // obtengo el identificador el servicio
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this, "El IdServicio debe ser un número válido", "IdServicio no válido", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		
		//nombre
		nombre = txtNombre.getText();
		
		//host
		host = txtHost.getText();
				
		//puerto
		try {
			if (txtPuerto.getText().isEmpty()) // en el alta será un string vacío
				puerto = -1;
			else
				puerto = Integer.valueOf(txtPuerto.getText()); 
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this, "El Puerto debe ser un número válido", "Puerto no válido", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		
		
		//tipoServicio
		if (cbTipo.getSelectedItem()!= null)
			tipo = (Servicio.TipoServicio) cbTipo.getSelectedItem();
		else {
			JOptionPane.showMessageDialog(this, "Debe seleccionar el tipo de servicio", "Tipo de servicio no válido", JOptionPane.ERROR_MESSAGE);
			return null;
		}
			
		//clave
		clave = "";
		for (int i = 0; i < pwdClave.getPassword().length; i++)
			clave += pwdClave.getPassword()[i];
		return new Servicio(idServicio, nombre, host, puerto, tipo, clave);
	} // campos2Servicio
	
	
	
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
			lblTituloTabla.setText("Servicios filtrados por búsqueda");
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
			lblTituloTabla.setText("Listado de servicios");
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

        panelCamposMesa = new javax.swing.JPanel();
        txtIdServicio = new javax.swing.JTextField();
        cbTipo = new javax.swing.JComboBox<>();
        btnGuardar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        txtHost = new javax.swing.JTextField();
        txtPuerto = new javax.swing.JTextField();
        pwdClave = new javax.swing.JPasswordField();
        panelTabla = new javax.swing.JPanel();
        lblTituloTabla = new javax.swing.JLabel();
        btnResetearFiltro = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaServicios = new javax.swing.JTable();
        botonera = new javax.swing.JPanel();
        btnAgregar = new javax.swing.JButton();
        btnModificar = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        btnBuscar = new javax.swing.JButton();
        btnSalir = new javax.swing.JButton();
        cboxOrden = new javax.swing.JComboBox<>();
        panelMesas = new javax.swing.JPanel();
        panelMesasAsignadas = new javax.swing.JScrollPane();
        tablaMesasAsignadas = new javax.swing.JTable();
        lblTituloTabla1 = new javax.swing.JLabel();
        panelMesasNoAsignadas = new javax.swing.JScrollPane();
        tablaMesasNoAsignadas = new javax.swing.JTable();
        lblTituloTabla2 = new javax.swing.JLabel();
        btnAsignarMesa = new javax.swing.JButton();
        btnDesasignarMesa = new javax.swing.JButton();

        panelCamposMesa.setBackground(new java.awt.Color(153, 153, 255));
        panelCamposMesa.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N

        txtIdServicio.setEditable(false);
        txtIdServicio.setBorder(javax.swing.BorderFactory.createTitledBorder("Id Servicio"));
        txtIdServicio.setMinimumSize(new java.awt.Dimension(16, 42));
        txtIdServicio.setName(""); // NOI18N
        txtIdServicio.setPreferredSize(new java.awt.Dimension(16, 42));

        cbTipo.setBorder(javax.swing.BorderFactory.createTitledBorder("Tipo"));
        cbTipo.setEnabled(false);
        cbTipo.setMinimumSize(new java.awt.Dimension(28, 42));
        cbTipo.setPreferredSize(new java.awt.Dimension(28, 42));

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
        jLabel7.setText("Gestión de Servicios");

        txtNombre.setEditable(false);
        txtNombre.setBorder(javax.swing.BorderFactory.createTitledBorder("Nombre"));
        txtNombre.setPreferredSize(new java.awt.Dimension(16, 42));

        txtHost.setEditable(false);
        txtHost.setBorder(javax.swing.BorderFactory.createTitledBorder("Host"));
        txtHost.setMinimumSize(new java.awt.Dimension(16, 42));
        txtHost.setPreferredSize(new java.awt.Dimension(16, 42));

        txtPuerto.setEditable(false);
        txtPuerto.setBorder(javax.swing.BorderFactory.createTitledBorder("Puerto"));
        txtPuerto.setMinimumSize(new java.awt.Dimension(16, 42));
        txtPuerto.setPreferredSize(new java.awt.Dimension(16, 42));

        pwdClave.setEditable(false);
        pwdClave.setBorder(javax.swing.BorderFactory.createTitledBorder("Clave"));
        pwdClave.setMinimumSize(new java.awt.Dimension(16, 42));
        pwdClave.setPreferredSize(new java.awt.Dimension(121, 42));

        javax.swing.GroupLayout panelCamposMesaLayout = new javax.swing.GroupLayout(panelCamposMesa);
        panelCamposMesa.setLayout(panelCamposMesaLayout);
        panelCamposMesaLayout.setHorizontalGroup(
            panelCamposMesaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCamposMesaLayout.createSequentialGroup()
                .addGroup(panelCamposMesaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panelCamposMesaLayout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addGroup(panelCamposMesaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelCamposMesaLayout.createSequentialGroup()
                                .addComponent(txtIdServicio, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtNombre, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(panelCamposMesaLayout.createSequentialGroup()
                                .addComponent(txtHost, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtPuerto, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 1, Short.MAX_VALUE))))
                    .addGroup(panelCamposMesaLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(cbTipo, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pwdClave, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(1, 1, 1)))
                .addGap(20, 20, 20))
            .addGroup(panelCamposMesaLayout.createSequentialGroup()
                .addGroup(panelCamposMesaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelCamposMesaLayout.createSequentialGroup()
                        .addGap(85, 85, 85)
                        .addComponent(jLabel7))
                    .addGroup(panelCamposMesaLayout.createSequentialGroup()
                        .addGap(67, 67, 67)
                        .addComponent(btnGuardar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCancelar)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelCamposMesaLayout.setVerticalGroup(
            panelCamposMesaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCamposMesaLayout.createSequentialGroup()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelCamposMesaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtNombre, javax.swing.GroupLayout.DEFAULT_SIZE, 55, Short.MAX_VALUE)
                    .addComponent(txtIdServicio, javax.swing.GroupLayout.DEFAULT_SIZE, 55, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(panelCamposMesaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtHost, javax.swing.GroupLayout.DEFAULT_SIZE, 55, Short.MAX_VALUE)
                    .addComponent(txtPuerto, javax.swing.GroupLayout.DEFAULT_SIZE, 55, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(panelCamposMesaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(panelCamposMesaLayout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(cbTipo, javax.swing.GroupLayout.DEFAULT_SIZE, 55, Short.MAX_VALUE))
                    .addComponent(pwdClave, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 39, Short.MAX_VALUE)
                .addGroup(panelCamposMesaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnGuardar)
                    .addComponent(btnCancelar))
                .addContainerGap())
        );

        panelTabla.setBackground(new java.awt.Color(153, 153, 255));

        lblTituloTabla.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lblTituloTabla.setText("Listado de Servicios");
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

        tablaServicios.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id", "Nombre", "Host", "Puerto", "Tipo"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class, java.lang.Integer.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tablaServicios.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaServiciosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tablaServicios);
        if (tablaServicios.getColumnModel().getColumnCount() > 0) {
            tablaServicios.getColumnModel().getColumn(0).setPreferredWidth(30);
            tablaServicios.getColumnModel().getColumn(0).setMaxWidth(30);
            tablaServicios.getColumnModel().getColumn(1).setMinWidth(150);
            tablaServicios.getColumnModel().getColumn(1).setPreferredWidth(50);
            tablaServicios.getColumnModel().getColumn(1).setMaxWidth(150);
            tablaServicios.getColumnModel().getColumn(2).setResizable(false);
            tablaServicios.getColumnModel().getColumn(2).setPreferredWidth(30);
            tablaServicios.getColumnModel().getColumn(3).setPreferredWidth(60);
            tablaServicios.getColumnModel().getColumn(3).setMaxWidth(60);
            tablaServicios.getColumnModel().getColumn(4).setResizable(false);
            tablaServicios.getColumnModel().getColumn(4).setPreferredWidth(20);
        }

        javax.swing.GroupLayout panelTablaLayout = new javax.swing.GroupLayout(panelTabla);
        panelTabla.setLayout(panelTablaLayout);
        panelTablaLayout.setHorizontalGroup(
            panelTablaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTablaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelTablaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 516, Short.MAX_VALUE)
                    .addGroup(panelTablaLayout.createSequentialGroup()
                        .addComponent(lblTituloTabla)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnResetearFiltro)))
                .addContainerGap())
        );
        panelTablaLayout.setVerticalGroup(
            panelTablaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTablaLayout.createSequentialGroup()
                .addGroup(panelTablaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTituloTabla)
                    .addComponent(btnResetearFiltro))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 251, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(39, Short.MAX_VALUE))
        );

        botonera.setBackground(new java.awt.Color(153, 153, 255));

        btnAgregar.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnAgregar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/mesero1_32x32.png"))); // NOI18N
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

        cboxOrden.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "por Id Servicio", "por Nombre", "por Tipo" }));
        cboxOrden.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Ordenado", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        cboxOrden.setMinimumSize(new java.awt.Dimension(104, 50));
        cboxOrden.setPreferredSize(new java.awt.Dimension(104, 50));
        cboxOrden.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboxOrdenActionPerformed(evt);
            }
        });

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
                .addComponent(cboxOrden, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnSalir)
                .addContainerGap())
        );
        botoneraLayout.setVerticalGroup(
            botoneraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(botoneraLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(botoneraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAgregar, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnModificar, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSalir, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, botoneraLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cboxOrden, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        panelMesas.setBackground(new java.awt.Color(153, 255, 204));

        tablaMesasAsignadas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null},
                {null},
                {null},
                {null}
            },
            new String [] {
                "Mesa"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tablaMesasAsignadas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaMesasAsignadasMouseClicked(evt);
            }
        });
        panelMesasAsignadas.setViewportView(tablaMesasAsignadas);
        if (tablaMesasAsignadas.getColumnModel().getColumnCount() > 0) {
            tablaMesasAsignadas.getColumnModel().getColumn(0).setResizable(false);
        }

        lblTituloTabla1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lblTituloTabla1.setText("Mesas asignadas al mesero");
        lblTituloTabla1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        tablaMesasNoAsignadas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Mesa", "IdMesero", "Nombre"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tablaMesasNoAsignadas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaMesasNoAsignadasMouseClicked(evt);
            }
        });
        panelMesasNoAsignadas.setViewportView(tablaMesasNoAsignadas);
        if (tablaMesasNoAsignadas.getColumnModel().getColumnCount() > 0) {
            tablaMesasNoAsignadas.getColumnModel().getColumn(0).setMinWidth(50);
            tablaMesasNoAsignadas.getColumnModel().getColumn(1).setMinWidth(50);
            tablaMesasNoAsignadas.getColumnModel().getColumn(2).setMinWidth(200);
        }

        lblTituloTabla2.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lblTituloTabla2.setText("Mesas no asignadas al mesero");
        lblTituloTabla2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        btnAsignarMesa.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/flecha_izquierda2_32x32.png"))); // NOI18N
        btnAsignarMesa.setText("    Asignar mesa");
        btnAsignarMesa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAsignarMesaActionPerformed(evt);
            }
        });

        btnDesasignarMesa.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/flecha_derecha2_32x32.png"))); // NOI18N
        btnDesasignarMesa.setText("Desasignar mesa");
        btnDesasignarMesa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDesasignarMesaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelMesasLayout = new javax.swing.GroupLayout(panelMesas);
        panelMesas.setLayout(panelMesasLayout);
        panelMesasLayout.setHorizontalGroup(
            panelMesasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMesasLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelMesasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblTituloTabla1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelMesasAsignadas, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panelMesasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnDesasignarMesa, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnAsignarMesa, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(60, 60, 60)
                .addGroup(panelMesasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelMesasNoAsignadas, javax.swing.GroupLayout.PREFERRED_SIZE, 386, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTituloTabla2, javax.swing.GroupLayout.PREFERRED_SIZE, 288, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        panelMesasLayout.setVerticalGroup(
            panelMesasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMesasLayout.createSequentialGroup()
                .addComponent(lblTituloTabla1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelMesasAsignadas, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addGap(5, 5, 5))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelMesasLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(lblTituloTabla2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelMesasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelMesasNoAsignadas, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelMesasLayout.createSequentialGroup()
                        .addComponent(btnAsignarMesa)
                        .addGap(28, 28, 28)
                        .addComponent(btnDesasignarMesa)))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(panelCamposMesa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelTabla, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(botonera, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelMesas, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(panelTabla, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelCamposMesa, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(botonera, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelMesas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

	
	
//================================================================================
//================================================================================
	
	/** permite editar en los campos, habilita boton de guardar/cancelar y deshabilita otros botones.
	    El alta verdadera lo realiza el botón de guardar (si no eligió cancelar) */
    private void btnAgregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarActionPerformed
        tipoEdicion = TipoEdicion.AGREGAR;  //para que el boton guardar sepa que estoy queriendo agregar una servicio
        limpiarCampos(); //Pongo todos los campos de texto en blanco
        habilitoParaEditar();
    }//GEN-LAST:event_btnAgregarActionPerformed

	
	/** 
	 * Permite editar en los campos, habilita boton de guardar/cancelar y deshabilita otros botones.
	 * La modificación verdadera lo realiza el botón de guardar (si no eligió cancelar)
	 */ 
    private void btnModificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModificarActionPerformed
        tipoEdicion = TipoEdicion.MODIFICAR; //para que el boton guardar sepa que estoy queriendo modificar un servicio
        habilitoParaEditar();
    }//GEN-LAST:event_btnModificarActionPerformed

	
	/** 
	 * Elimina la servicio seleccionado de la tabla. 
	 * Como no queda ninguna seleccionado, deshabilito botones btnModificar y btnEliminar
	 */
    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
        if ( eliminarServicio() ) { // si pudo eliminar
            limpiarCampos(); //Pongo todos los campos de texto en blanco
            btnModificar.setEnabled(false); //deshabilito botón modificar
            btnEliminar.setEnabled(false);  //deshabilito botón eliminar
            cargarMapaServicios();
            cargarTabla();
        }
    }//GEN-LAST:event_btnEliminarActionPerformed

	
	
	/**
	 * Permite editar en los campos, cambia el botón guardar a buscar, 
	 * habilita boton de guardar/cancelar y deshabilita otros botones.
	 * La búsqueda verdadera lo realiza el botón de guardar (si no eligió cancelar)
	 */
    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarActionPerformed
        tipoEdicion = TipoEdicion.BUSCAR; //para que el boton guardar sepa que estoy queriendo buscar un servicio
        limpiarCampos();
        botonGuardarComoBuscar(); //cambio icono y texto del btnGuardar a "Buscar"
        habilitoParaBuscar();
    }//GEN-LAST:event_btnBuscarActionPerformed

	
	
	/** Cierra la ventana (termina CrudServicios */
    private void btnSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalirActionPerformed
        dispose();//cierra la ventana
    }//GEN-LAST:event_btnSalirActionPerformed

	
	
/**
 * Permite ordenar la lista de servicios por el criterio de este combo box
 * @param evt 
 */	
    private void cboxOrdenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboxOrdenActionPerformed
        if (cboxOrden.getSelectedIndex() == 0)
			ordenacion = OrdenacionServicio.PORIDSERVICIO;
        else if (cboxOrden.getSelectedIndex() == 1)
			ordenacion = OrdenacionServicio.PORNOMBRESERVICIO;
		else if (cboxOrden.getSelectedIndex() == 2)
			ordenacion = OrdenacionServicio.PORTIPOSERVICIO;
        else // por las dudas que no eligio uno correcto
			ordenacion = OrdenacionServicio.PORIDSERVICIO;

        cargarMapaServicios();
        cargarTabla();
        limpiarCampos();
        botonGuardarComoGuardar();
        deshabilitoParaEditar();
    }//GEN-LAST:event_cboxOrdenActionPerformed

	
	
	
	
	
	/** con los campos de texto de la pantalla hace un agregarServicio, modificarServicio o buscarServicio
	    en base a la variable tipoEdicion, ya sea AGREGAR, MODIFICAR o BUSCAR */
    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        if ( tipoEdicion == TipoEdicion.AGREGAR ){ //agregar el servicio
            agregarServicio();
            resetearFiltro();
        } else if ( tipoEdicion == TipoEdicion.MODIFICAR ) { // modificar el servicio
            modificarServicio();
            resetearFiltro();
        } else { // tipoEdicion = BUSCAR: quiere buscar un servicio
            buscarServicio();
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
        cargarMapaServicios();
        cargarTabla();
        limpiarCampos();
        botonGuardarComoGuardar();//por si estaba buscando cambio icono y texto del btnGuardar a "Guardar"
        deshabilitoParaEditar();
    }//GEN-LAST:event_btnResetearFiltroActionPerformed

	
	
	/** al hacer clik en una fila de la tabla, queda seleccionado una servicio.
	 * Entonces habilita los botones de eliminar y modificar
	 * @param evt 
	 */
    private void tablaServiciosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaServiciosMouseClicked
       //tabla.addRowSelectionInterval(filaTabla, filaTabla); //selecciono esa fila de la tabla
		int numfila = tablaServicios.getSelectedRow();
		if (numfila != -1) {			
			btnEliminar.setEnabled(true); // habilito el botón de eliminar
			btnModificar.setEnabled(true); // habilito el botón de modificar
			
			filaTabla2Campos(numfila); // cargo los campos de texto de la pantalla con datos de la fila seccionada de la tabla
			cargarTablaMesas( (Integer) tablaServicios.getValueAt(numfila, 0) );
			if (tablaMesasAsignadas.getRowCount() > 0)
				tablaMesasAsignadas.removeRowSelectionInterval(0, tablaMesasAsignadas.getRowCount()-1); //des-selecciono las filas de la tabla
			tablaMesasAsignadas.setEnabled(true);
			if (tablaMesasNoAsignadas.getRowCount() > 0)
				tablaMesasNoAsignadas.removeRowSelectionInterval(0, tablaMesasNoAsignadas.getRowCount()-1); //des-selecciono las filas de la tabla
			tablaMesasNoAsignadas.setEnabled(true);
		}  
    }//GEN-LAST:event_tablaServiciosMouseClicked

    private void btnDesasignarMesaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDesasignarMesaActionPerformed
        if (tablaMesasAsignadas.getSelectedRow() != -1){ // si hay alguna fila seleccionada
			btnDesasignarMesa.setEnabled(false); // deshabilito botón Desasignar.
        }
        int numfilaAsignadas = tablaMesasAsignadas.getSelectedRow();
        if (numfilaAsignadas != -1) { //si hay alguna fila seleccionada en la tabla de mesas asignadas
			int idMesa = (Integer)tablaMesasAsignadas.getValueAt(numfilaAsignadas, 0);//averiguamos el idMesa
			int idMesero = (Integer) tablaServicios.getValueAt(tablaServicios.getSelectedRow(), 0);
			//modifico el mesero de la mesa, poniendolo a 0 (para que almacene null)
			Mesa mesa = mesaData.getMesa(idMesa);
			mesa.setIdMesero(0);
			mesaData.modificarMesa(mesa);
			
			//actualizamos las listas y tablas de mesas
			cargarListaMesas();
			cargarTablaMesas( idMesero );
		}
    }//GEN-LAST:event_btnDesasignarMesaActionPerformed

    private void btnAsignarMesaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAsignarMesaActionPerformed
        if (tablaMesasNoAsignadas.getSelectedRow() != -1){ // si hay alguna fila seleccionada
			btnAsignarMesa.setEnabled(false); // deshabilito botón Desasignar.
        }
        int numfilaNoAsignadas = tablaMesasNoAsignadas.getSelectedRow();
        if (numfilaNoAsignadas != -1) { //si hay alguna fila seleccionada en la tabla de mesas asignadas
			int idMesa = (Integer)tablaMesasNoAsignadas.getValueAt(numfilaNoAsignadas, 0);//averiguamos el idMesa
			int idMesero = (Integer) tablaServicios.getValueAt(tablaServicios.getSelectedRow(), 0);
			
			//modifico el mesero de la mesa, poniendolo a 0 (para que almacene null)
			Mesa mesa = mesaData.getMesa(idMesa);
			mesa.setIdMesero(idMesero);
			mesaData.modificarMesa(mesa);
			
			//actualizamos las listas y tablas de mesas
			cargarListaMesas();
			cargarTablaMesas( idMesero );
		}
    }//GEN-LAST:event_btnAsignarMesaActionPerformed

    private void tablaMesasNoAsignadasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaMesasNoAsignadasMouseClicked
        if (tablaMesasNoAsignadas.getSelectedRow() != -1){ // si hay alguna fila seleccionada
			btnAsignarMesa.setEnabled(false); // deshabilito botón Asignar mesa
        }
        int numfila = tablaMesasNoAsignadas.getSelectedRow();
        if (numfila != -1) { //si hay alguna fila seleccionada en la tabla de mesas no asignadas
			btnAsignarMesa.setEnabled(true);
        } 
    }//GEN-LAST:event_tablaMesasNoAsignadasMouseClicked

    private void tablaMesasAsignadasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaMesasAsignadasMouseClicked
        if (tablaMesasAsignadas.getSelectedRow() != -1){ // si hay alguna fila seleccionada
			btnDesasignarMesa.setEnabled(false); // deshabilito botón Desasignar
        }
        int numfila = tablaMesasAsignadas.getSelectedRow();
        if (numfila != -1) { //si hay alguna fila seleccionada en la tabla de mesas asignadas
			btnDesasignarMesa.setEnabled(true);
        } 
    }//GEN-LAST:event_tablaMesasAsignadasMouseClicked


//================================================================================
//================================================================================
	
		
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel botonera;
    private javax.swing.JButton btnAgregar;
    private javax.swing.JButton btnAsignarMesa;
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnDesasignarMesa;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnModificar;
    private javax.swing.JButton btnResetearFiltro;
    private javax.swing.JButton btnSalir;
    private javax.swing.JComboBox<Servicio.TipoServicio> cbTipo;
    private javax.swing.JComboBox<String> cboxOrden;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblTituloTabla;
    private javax.swing.JLabel lblTituloTabla1;
    private javax.swing.JLabel lblTituloTabla2;
    private javax.swing.JPanel panelCamposMesa;
    private javax.swing.JPanel panelMesas;
    private javax.swing.JScrollPane panelMesasAsignadas;
    private javax.swing.JScrollPane panelMesasNoAsignadas;
    private javax.swing.JPanel panelTabla;
    private javax.swing.JPasswordField pwdClave;
    private javax.swing.JTable tablaMesasAsignadas;
    private javax.swing.JTable tablaMesasNoAsignadas;
    private javax.swing.JTable tablaServicios;
    private javax.swing.JTextField txtHost;
    private javax.swing.JTextField txtIdServicio;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtPuerto;
    // End of variables declaration//GEN-END:variables
} // CrudServicios



//================================================================================
//================================================================================
	


/**
 * Es una clase para agrupar y almacenar los datos con los que se filtra una búsqueda
 * @author John David Molina Velarde
 */
class FiltroServicios{
	int idServicio;
	String nombreServicio;
	Servicio.TipoServicio tipo;
	boolean estoyFiltrando;

	public FiltroServicios() { // constructor
		idServicio = -1;
		nombreServicio = "";
		tipo = null;
		estoyFiltrando = false;
	} // constructor FiltroServicios
} //FiltroServicios