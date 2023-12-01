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


import accesoadatos.CategoriaData;
import accesoadatos.ProductoData;
import accesoadatos.ProductoData.OrdenacionProducto;
import accesoadatos.ProductoData;
import entidades.Servicio;
import accesoadatos.ServicioData;
import utiles.Utils;
import entidades.Categoria;
import entidades.Producto;
import java.awt.Color;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *							 
 * @author John David Molina Velarde, Leticia Mores, Enrique Germán Martínez, Carlos Eduardo Beltrán
 */
public class CrudProductos extends javax.swing.JInternalFrame {
	private javax.swing.JDesktopPane escritorio;
	DefaultTableModel modeloTabla;
	private List<Producto> listaProductos;
	private ProductoData productoData;	
	private Servicio servicioSinAsignar = new Servicio(0, "SIN ASIGNAR", "", 0, Servicio.TipoServicio.MESERO, "");
	private LinkedHashMap<Integer, Servicio> mapaServicios;
	private LinkedHashMap<Integer, Categoria> mapaCategorias;
	private enum TipoEdicion {AGREGAR, MODIFICAR, BUSCAR};
	private TipoEdicion tipoEdicion = TipoEdicion.AGREGAR; //para que el boton guardar sepa que estoy queriendo hacer:
														   // Si con los campos voy a agregar, modificar o buscar una producto
	private OrdenacionProducto ordenacion = OrdenacionProducto.PORIDPRODUCTO; // defino el tipo de orden por defecto 
	private FiltroProductos filtro = new FiltroProductos();  //el filtro de búsqueda
	
	
	public CrudProductos(javax.swing.JDesktopPane escritorio) {
		initComponents();
		this.escritorio = escritorio;
		productoData = new ProductoData(); 
		modeloTabla = (DefaultTableModel) tablaProductos.getModel();
		cargarListaProductos(); //carga la base de datos
		cargarMapaCategorias();
		cargarMapaServicios();
		cargarTabla(); // cargo la tabla con las productos
	}

	
	/** carga la lista de productos de la BD */
	private void cargarListaProductos(){ 
		if (filtro.estoyFiltrando) 
			listaProductos = productoData.getListaProductosXCriterioDeBusqueda(
				//idProducto,		nombre,		stock, precio, disponible, 	idCategoria,	 despachadoPor,			ordenacion
				filtro.idProducto, filtro.nombre, -1, -1.0,		null,		filtro.categoria, filtro.despachadoPor, ordenacion);
		else
			listaProductos = productoData.getListaProductos(ordenacion);
	}//cargarListaProductos
	
	
		
	/** carga el mapa de categorias de la BD y carga el combo box*/
	private void cargarMapaCategorias(){ 
		//cargo la lista de categorías
		CategoriaData categoriaData = new CategoriaData();
		List<Categoria> listaCategorias = categoriaData.getListaCategorias();
		
		//con esa lista genero el mapa de categorías
		mapaCategorias = new LinkedHashMap();
		listaCategorias.stream().forEach(categoria -> mapaCategorias.put(categoria.getIdCategoria(), categoria));
		
		//borro el combo box de categorias
		int cantidad = cbCategoria.getItemCount();
		for (int i = 0; i < cantidad; i++){
			cbCategoria.removeItemAt(0);
		}
		
		//ahora cargo el combo box de categorias con la nuevas categorias
		listaCategorias.stream().forEach( categoria -> cbCategoria.addItem(categoria) );
	}//cargarMapaCategorias
	
	
	
	/** carga el mapa de Servicios de la BD y carga el combo box */
	private void cargarMapaServicios(){ 
		//cargo la lista de servicios
		ServicioData servicioData = new ServicioData();
		List<Servicio> listaServicios = servicioData.getListaServicios();
		listaServicios.add(0, servicioSinAsignar);// para cuando no hay un mesero asignado a la mesa.
		
		//con esa lista genero el mapa de servicios
		mapaServicios = new LinkedHashMap();
		listaServicios.stream().forEach( servicio -> mapaServicios.put(servicio.getIdServicio(), servicio) );
		
		//borro el combo box de servicios cbDespachadoPor
		int cantidad = cbDespachadoPor.getItemCount();
		for (int i = 0; i < cantidad; i++){
			cbDespachadoPor.removeItemAt(0);
		}
		
		//tambien cargo el combo box de servicios
		listaServicios.stream().forEach( servicio -> cbDespachadoPor.addItem(servicio) );
	}//cargarMapaServicios
	
	
	
	
	/** carga productos de la lista a la tabla */
	private void cargarTabla(){ 
		//borro las filas de la tabla
		for (int fila = modeloTabla.getRowCount() -  1; fila >= 0; fila--)
			modeloTabla.removeRow(fila);
		
		//cargo los productos de listaProductos a la tabla
		for (Producto producto : listaProductos) {
			//System.out.println(producto + " idCategoria: " + producto.getIdCategoria() + " mapaCategorias:" + mapaCategorias.get(producto.getIdCategoria() ));
			modeloTabla.addRow(new Object[] {
				producto.getIdProducto(),
				producto.getNombre(),
				producto.getDescripcion(),
				producto.getStock(),
				producto.getPrecio(),
				producto.getDisponible(),
				mapaCategorias.get(producto.getIdCategoria()), // almaceno el objeto categoria
				mapaServicios.get(producto.getDespachadoPor()) // almaceno el objeto servicio
			}
			);
		}
		
		//como no hay fila seleccionada, deshabilito el botón Eliminar y Modificar
		if (tablaProductos.getSelectedRow() == -1) {// si no hay alguna fila seleccionada
			btnEliminar.setEnabled(false); // deshabilito el botón de eliminar
			btnModificar.setEnabled(false); // deshabilito el botón de Modificar
		}
	} //cargarTabla
	
	
	/** 
	 * Elimina al producto seleccionado de la lista y la bd. 
	 * @return Devuelve true si pudo eliminarlo
	 */
	private boolean eliminarProducto(){ 
		int fila = tablaProductos.getSelectedRow();
        if (fila != -1) { // Si hay alguna fila seleccionada
			int idProducto = Integer.parseInt(txtIdProducto.getText());
			if (productoData.bajaProducto(idProducto)){ 
				listaProductos.remove(fila);
				return true;
			} else
				return false;
            //tabla.removeRowSelectionInterval(0, tabla.getRowCount()-1); //des-selecciono las filas de la tabla
        } else {
			JOptionPane.showMessageDialog(this, "Debe seleccionar un producto para eliminar", "No ha seleccionado ningún producto", JOptionPane.ERROR_MESSAGE);
			return false;
		}
	} //eliminarProducto
	
	
	/**
	 * si no hay errores en los campos, agrega un producto con dichos campos. 
	 * @return Devuelve true si pudo agregarlo
	 */
	private boolean agregarProducto(){
		Producto producto = campos2Producto();
		if ( producto != null ) {
			if ( productoData.altaProducto(producto) ) {// si pudo dar de alta al producto
				cargarListaProductos();
				cargarTabla();
				return true;
			} else {
				JOptionPane.showMessageDialog(this, "Debe completar correctamente todos los datos del producto para agregarlo", "No se puede agregar", JOptionPane.ERROR_MESSAGE);
				return false;
			}
		} else {
			// si producto es null, no pudo transformarlo a producto. Sigo editando
			return false;
		}
	} //agregarProducto

	
	/** si no hay errores en los campos, modifica un producto con dichos campos */
	private void modificarProducto() {
		Producto producto = campos2Producto();
		if ( producto != null ) {
			if ( productoData.modificarProducto(producto) )  {// si pudo  modificar al producto
				cargarListaProductos();
				cargarTabla();
			} else 
				JOptionPane.showMessageDialog(this, "Debe completar correctamente todos los datos de la producto para modificarla", "No se puede agregar", JOptionPane.ERROR_MESSAGE);			
		} else {
			// si producto es null, no pudo transformarlo a producto. Sigo editando
			//JOptionPane.showMessageDialog(this, "campos2Producto devolvió un null", "no se pudo agregar", JOptionPane.ERROR_MESSAGE);
		}	
	} //modificarProducto
      
	
	
	
	/**
	 * Busca al producto por id, por capacidad, por estado o por idMesero (o por 
	 * combinación de dichos campos). 
	 * El criterio para usar un campo en la búsqueda es que no esté en blanco. 
	 * Es decir, si tiene datos, se buscará por ese dato. Por ejemplo, si puso 
	 * el id, buscará por id. Si puso el cantidad, buscará por cantidad. 
	 * Si puso el cantidad y idMesero, buscara por cantidad and idMesero.
	 * 
	 * @return devuelve true sio pudo usar algún criterio de búsqueda
	 */
	private boolean buscarProducto(){ 
		// cargo los campos de texto id, nombre y categoría para buscar por esos criterios
		int idProducto;
		String nombre;
		int idCategoria, despachadoPor;
		
		//idProducto
		try {
			if (txtIdProducto.getText().isEmpty()) // si está vacío no se usa para buscar
				idProducto = -1;
			else
				idProducto = Integer.valueOf(txtIdProducto.getText()); //no vacío, participa del criterio de búsqueda
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this, "El Id debe ser un número válido", "Id no válido", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
		//nombre
		if (txtNombre.getText().isEmpty()) // si está vacío no se usa para buscar
			nombre = null;
		else
			nombre = String.valueOf(txtNombre.getText()); // no vacío, participa del criterio de búsqueda
				
        //categoria        
		Categoria categoria = (Categoria) cbCategoria.getSelectedItem();
		idCategoria = (categoria==null) ? -1 : categoria.getIdCategoria();
		
		//servicio
		Servicio servicio = (Servicio) cbDespachadoPor.getSelectedItem();
		despachadoPor = (servicio==null) ? -1 : servicio.getIdServicio();
		
		//testeo que hay al menos un criterio de búsqueda
		if ( idProducto==-1 && nombre==null && categoria==null && servicio==null )   {
			JOptionPane.showMessageDialog(this, "Debe ingresar algún criterio para buscar", "Ningun criterio de búsqueda", JOptionPane.ERROR_MESSAGE);
			return false;
		} else { //todo Ok. Buscar por alguno de los criterior de búsqueda
			filtro.idProducto = idProducto;
			filtro.nombre = nombre;
			filtro.categoria = idCategoria;
			filtro.despachadoPor = despachadoPor;
			filtro.estoyFiltrando = true;
			cargarListaProductos();
			cargarTabla();
			return true; // pudo buscar
		}
	} //buscarProducto
	

	
	
	
	/** deshabilito todos los botones y tabla, habilito guardar/cancelar */
	private void habilitoParaBuscar(){ 
		habilitoParaEditar();
		txtIdProducto.setEditable(true);
		txtDescripcion.setEditable(false);
		txtStock.setEditable(false);
		txtPrecio.setEditable(false);
		ckbDisponible.setEnabled(false);
	} //habilitoParaBuscar

	
		
	
	/** deshabilito todos los botones y tabla, habilito guardar/cancelar */
	private void habilitoParaEditar(){ 
		// deshabilito todos los botones (menos salir)
		btnAgregar.setEnabled(false);
		btnModificar.setEnabled(false); //deshabilito botón modificar
		btnEliminar.setEnabled(false);  //deshabilito botón eliminar
		btnBuscar.setEnabled(false);
		btnCategorias.setEnabled(false);
		cboxOrden.setEnabled(false);
		
		//Deshabilito la Tabla para que no pueda hacer click
		tablaProductos.setEnabled(false);
		
		//Habilito los botones guardar y cancelar
		btnGuardar.setEnabled(true); // este botón es el que realmente se encargará de agregegar el producto
		btnCancelar.setEnabled(true);
		
		//Habilito los campos para poder editar
		txtNombre.setEditable(true);
		txtDescripcion.setEditable(true);
		txtStock.setEditable(true);
		txtPrecio.setEditable(true);
		ckbDisponible.setEnabled(true);
		cbCategoria.setEnabled(true);
		cbDespachadoPor.setEnabled(true);
	} //habilitoParaEditar

	
	
	
	/** habilito todos los botones y tabla, deshabilito guardar/cancelar y modificar */
	private void deshabilitoParaEditar(){ 
		limpiarCampos(); //Pongo todos los campos de texto en blanco
		// habilito todos los botones (menos salir)
		btnAgregar.setEnabled(true);
		btnBuscar.setEnabled(true);
		btnCategorias.setEnabled(true);
		cboxOrden.setEnabled(true);
		
		//sigo deshabilitando los botones modificar y eliminar porque no hay una fila seleccionada.
		btnModificar.setEnabled(false); //deshabilito botón modificar
		btnEliminar.setEnabled(false);  //deshabilito botón eliminar
		
		//Habilito la Tabla para que pueda hacer click
		tablaProductos.setEnabled(true);
		
		//Deshabilito el boton guardar 
		btnGuardar.setEnabled(false);  
		botonGuardarComoGuardar(); //por si estaba buscando cambio icono y texto del btnGuardar a "Guardar"
		
		//deshabilito el boton cancelar
		btnCancelar.setEnabled(false);

		//deshabilito los campos para poder que no pueda editar
		txtIdProducto.setEditable(false);
		txtNombre.setEditable(false);
		txtDescripcion.setEditable(false);
		txtStock.setEditable(false);
		txtPrecio.setEditable(false);
		ckbDisponible.setEnabled(false);
		cbCategoria.setEnabled(false);
		cbDespachadoPor.setEnabled(false);
	} //deshabilitoParaEditar

	
	
	
	
	/** pongo los campos txtfield en blanco y deselecciono la fila de tabla */
	private void limpiarCampos(){
		//pongo los campos en blanco
		txtIdProducto.setText("");
		txtNombre.setText("");
        txtDescripcion.setText("");
		txtStock.setText("");
		txtPrecio.setText("");
		ckbDisponible.setSelected(false);
		cbCategoria.setSelectedIndex(-1);
		cbDespachadoPor.setSelectedIndex(-1);
		
		if (tablaProductos.getRowCount() > 0) 
			tablaProductos.removeRowSelectionInterval(0, tablaProductos.getRowCount()-1); //des-selecciono las filas de la tabla
	} // limpiarCampos




	/**
	 * cargo los datos de la fila indicada de la tabla a los campos de texto de la pantalla 
	 * @param numfila el número de fila a cargar a los campos
	 */
	private void filaTabla2Campos(int numfila){
		txtIdProducto.setText(tablaProductos.getValueAt(numfila, 0)+"");
		txtNombre.setText(tablaProductos.getValueAt(numfila, 1)+"");
		txtDescripcion.setText(tablaProductos.getValueAt(numfila, 2)+"");
		txtStock.setText(tablaProductos.getValueAt(numfila, 3)+"");
		txtPrecio.setText(tablaProductos.getValueAt(numfila,4)+"");
		ckbDisponible.setSelected((Boolean) tablaProductos.getValueAt(numfila,5) );
		cbCategoria.setSelectedItem((Categoria) tablaProductos.getValueAt(numfila, 6));
		cbDespachadoPor.setSelectedItem((Servicio) tablaProductos.getValueAt(numfila, 7));
	} //filaTabla2Campos


	
	
	/**
	 * Cargo los campos de texto de la pantalla a un objeto tipo Producto
	 * @return El Producto devuelto. Si hay algún error, devuelve null
	 */
	private Producto campos2Producto(){ 
		int idProducto;
		String nombre, descripcion;
		int stock;
		double precio;
		boolean disponible;
		int idCategoria;
		int idDespachadopor;
		
		//idProducto
		try {
			if (txtIdProducto.getText().isEmpty()) // en el alta será un string vacío
				idProducto = -1;
			else
				idProducto = Integer.valueOf(txtIdProducto.getText()); // obtengo el identificador el producto
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this, "El IdProducto debe ser un número válido", "IdProducto no válido", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		
		//nombre y descripcion
		nombre = txtNombre.getText();
		descripcion = txtDescripcion.getText();
		
		//stock
		try {
			stock = Integer.valueOf(txtStock.getText());
				
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this, "El stock debe ser un número válido", "Stock no válido", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		

		//precio
		try {
			if (txtPrecio.getText().isEmpty()) // en el alta será un string vacío
				precio = -1.0;
			else
				precio = Double.valueOf(txtPrecio.getText());
				
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this, "El precio debe ser un número válido", "Precio no válido", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		
		//disponible
		disponible = ckbDisponible.isSelected();
		
		//idCategoria y idDespachadoPor
		idCategoria = (cbCategoria.getSelectedItem()==null) ? 0 : ((Categoria) cbCategoria.getSelectedItem()).getIdCategoria();
		idDespachadopor = (cbDespachadoPor.getSelectedItem()==null) ? 0 : ((Servicio)cbDespachadoPor.getSelectedItem()).getIdServicio();
	
		Producto producto = new Producto(idProducto, nombre, descripcion, stock, precio, disponible, idCategoria, idDespachadopor);
		//System.out.println("Campos2Producot: " + producto);
		return producto;
	} // campos2Producto
        
	
	
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
			lblTituloTabla.setText("Listado de productos filtrados por búsqueda");
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
			lblTituloTabla.setText("Listado de productos");
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
        txtIdProducto = new javax.swing.JTextField();
        btnGuardar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        txtDescripcion = new javax.swing.JTextField();
        txtStock = new javax.swing.JTextField();
        txtPrecio = new javax.swing.JTextField();
        ckbDisponible = new javax.swing.JCheckBox();
        cbCategoria = new javax.swing.JComboBox<>();
        cbDespachadoPor = new javax.swing.JComboBox<>();
        panelTabla = new javax.swing.JPanel();
        lblTituloTabla = new javax.swing.JLabel();
        btnResetearFiltro = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaProductos = new javax.swing.JTable();
        botonera = new javax.swing.JPanel();
        btnAgregar = new javax.swing.JButton();
        btnModificar = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        btnBuscar = new javax.swing.JButton();
        btnSalir = new javax.swing.JButton();
        cboxOrden = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        btnCategorias = new javax.swing.JButton();

        panelCamposMesa.setBackground(new java.awt.Color(153, 153, 255));
        panelCamposMesa.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N

        txtIdProducto.setEditable(false);
        txtIdProducto.setBorder(javax.swing.BorderFactory.createTitledBorder("Id Producto"));

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
        jLabel7.setText("Gestión de Productos");

        txtNombre.setEditable(false);
        txtNombre.setBorder(javax.swing.BorderFactory.createTitledBorder("Nombre"));

        txtDescripcion.setEditable(false);
        txtDescripcion.setBorder(javax.swing.BorderFactory.createTitledBorder("Descripción"));

        txtStock.setEditable(false);
        txtStock.setBorder(javax.swing.BorderFactory.createTitledBorder("Stock"));

        txtPrecio.setEditable(false);
        txtPrecio.setBorder(javax.swing.BorderFactory.createTitledBorder("Precio"));

        ckbDisponible.setText("Disponible");
        ckbDisponible.setBorder(javax.swing.BorderFactory.createTitledBorder("Disponible"));
        ckbDisponible.setEnabled(false);

        cbCategoria.setBorder(javax.swing.BorderFactory.createTitledBorder("Categoría"));
        cbCategoria.setEnabled(false);

        cbDespachadoPor.setBorder(javax.swing.BorderFactory.createTitledBorder("Despachado por"));
        cbDespachadoPor.setEnabled(false);

        javax.swing.GroupLayout panelCamposMesaLayout = new javax.swing.GroupLayout(panelCamposMesa);
        panelCamposMesa.setLayout(panelCamposMesaLayout);
        panelCamposMesaLayout.setHorizontalGroup(
            panelCamposMesaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelCamposMesaLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel7)
                .addGap(51, 51, 51))
            .addGroup(panelCamposMesaLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(panelCamposMesaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cbCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, 261, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelCamposMesaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(panelCamposMesaLayout.createSequentialGroup()
                            .addComponent(btnGuardar)
                            .addGap(18, 18, 18)
                            .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(panelCamposMesaLayout.createSequentialGroup()
                            .addComponent(txtIdProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(txtDescripcion))
                    .addGroup(panelCamposMesaLayout.createSequentialGroup()
                        .addComponent(txtStock, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtPrecio, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ckbDisponible))
                    .addComponent(cbDespachadoPor, javax.swing.GroupLayout.PREFERRED_SIZE, 261, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(17, Short.MAX_VALUE))
        );
        panelCamposMesaLayout.setVerticalGroup(
            panelCamposMesaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCamposMesaLayout.createSequentialGroup()
                .addComponent(jLabel7)
                .addGap(18, 18, 18)
                .addGroup(panelCamposMesaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtIdProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDescripcion, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelCamposMesaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtStock, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPrecio, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ckbDisponible, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cbCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cbDespachadoPor, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelCamposMesaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnGuardar)
                    .addComponent(btnCancelar))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelTabla.setBackground(new java.awt.Color(153, 153, 255));

        lblTituloTabla.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lblTituloTabla.setText("Listado de Productos");
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

        tablaProductos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id", "Nombre", "Descripción", "Stock", "Precio", "Disponible", "Categoría", "Despachado por"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Double.class, java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tablaProductos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaProductosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tablaProductos);
        if (tablaProductos.getColumnModel().getColumnCount() > 0) {
            tablaProductos.getColumnModel().getColumn(0).setPreferredWidth(20);
            tablaProductos.getColumnModel().getColumn(0).setMaxWidth(70);
            tablaProductos.getColumnModel().getColumn(1).setResizable(false);
            tablaProductos.getColumnModel().getColumn(1).setPreferredWidth(70);
            tablaProductos.getColumnModel().getColumn(2).setResizable(false);
            tablaProductos.getColumnModel().getColumn(2).setPreferredWidth(150);
            tablaProductos.getColumnModel().getColumn(3).setPreferredWidth(40);
            tablaProductos.getColumnModel().getColumn(3).setMaxWidth(100);
            tablaProductos.getColumnModel().getColumn(4).setPreferredWidth(50);
            tablaProductos.getColumnModel().getColumn(4).setMaxWidth(100);
            tablaProductos.getColumnModel().getColumn(5).setPreferredWidth(60);
            tablaProductos.getColumnModel().getColumn(5).setMaxWidth(70);
            tablaProductos.getColumnModel().getColumn(6).setPreferredWidth(100);
            tablaProductos.getColumnModel().getColumn(6).setMaxWidth(120);
            tablaProductos.getColumnModel().getColumn(7).setPreferredWidth(100);
            tablaProductos.getColumnModel().getColumn(7).setMaxWidth(120);
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
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 367, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        botonera.setBackground(new java.awt.Color(153, 153, 255));

        btnAgregar.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnAgregar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/productoNuevo32x32.png"))); // NOI18N
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

        cboxOrden.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "por IdProducto", "por Nombre", "por Categoria" }));
        cboxOrden.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboxOrdenActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel6.setText("Ordenado");

        btnCategorias.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        btnCategorias.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/categorias32x32.png"))); // NOI18N
        btnCategorias.setText("Categorías");
        btnCategorias.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCategoriasActionPerformed(evt);
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
                .addGroup(botoneraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(cboxOrden, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnCategorias)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 88, Short.MAX_VALUE)
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
                    .addComponent(cboxOrden, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCategorias))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
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
        tipoEdicion = TipoEdicion.AGREGAR;  //para que el boton guardar sepa que estoy queriendo agregar una producto
        limpiarCampos(); //Pongo todos los campos de texto en blanco
        habilitoParaEditar();
    }//GEN-LAST:event_btnAgregarActionPerformed

	
	/** 
	 * Permite editar en los campos, habilita boton de guardar/cancelar y deshabilita otros botones.
	 * La modificación verdadera lo realiza el botón de guardar (si no eligió cancelar)
	 */ 
    private void btnModificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModificarActionPerformed
        tipoEdicion = TipoEdicion.MODIFICAR; //para que el boton guardar sepa que estoy queriendo modificar un producto
        habilitoParaEditar();
    }//GEN-LAST:event_btnModificarActionPerformed

	
	/** 
	 * Elimina la producto seleccionado de la tabla. 
	 * Como no queda ninguna seleccionado, deshabilito botones btnModificar y btnEliminar
	 */
    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
        if ( eliminarProducto() ) { // si pudo eliminar
            limpiarCampos(); //Pongo todos los campos de texto en blanco
            btnModificar.setEnabled(false); //deshabilito botón modificar
            btnEliminar.setEnabled(false);  //deshabilito botón eliminar
            cargarListaProductos();
            cargarTabla();
        }
    }//GEN-LAST:event_btnEliminarActionPerformed

	
	
	/**
	 * Permite editar en los campos, cambia el botón guardar a buscar, 
	 * habilita boton de guardar/cancelar y deshabilita otros botones.
	 * La búsqueda verdadera lo realiza el botón de guardar (si no eligió cancelar)
	 */
    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarActionPerformed
        tipoEdicion = TipoEdicion.BUSCAR; //para que el boton guardar sepa que estoy queriendo buscar un producto
        limpiarCampos();
        botonGuardarComoBuscar(); //cambio icono y texto del btnGuardar a "Buscar"
        habilitoParaBuscar();
    }//GEN-LAST:event_btnBuscarActionPerformed

	
	
	/** Cierra la ventana (termina CrudProductos */
    private void btnSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalirActionPerformed
        dispose();//cierra la ventana
    }//GEN-LAST:event_btnSalirActionPerformed

	
	
/**
 * Permite ordenar la lista de productos por el criterio de este combo box
 * @param evt 
 */	
    private void cboxOrdenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboxOrdenActionPerformed
        if (cboxOrden.getSelectedIndex() == 0)
			ordenacion = OrdenacionProducto.PORIDPRODUCTO;
        else if (cboxOrden.getSelectedIndex() == 1)
			ordenacion = OrdenacionProducto.PORNOMBRE;
        else 
			ordenacion = OrdenacionProducto.PORIDCATEGORIAYNOMBRE;

        cargarListaProductos();
        cargarTabla();
        limpiarCampos();
        botonGuardarComoGuardar();
        deshabilitoParaEditar();
    }//GEN-LAST:event_cboxOrdenActionPerformed
	
	
	
	
	/** con los campos de texto de la pantalla hace un agregarProducto, modificarProducto o buscarProducto
	    en base a la variable tipoEdicion, ya sea AGREGAR, MODIFICAR o BUSCAR */
    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        if ( tipoEdicion == TipoEdicion.AGREGAR ){ //agregar el producto
            agregarProducto();
            resetearFiltro();
        } else if ( tipoEdicion == TipoEdicion.MODIFICAR ) { // modificar el producto
            modificarProducto(); 
            resetearFiltro();
        } else { // tipoEdicion = BUSCAR: quiere buscar un producto
            buscarProducto();
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
        cargarListaProductos();
        cargarTabla();
        limpiarCampos();
        botonGuardarComoGuardar();//por si estaba buscando cambio icono y texto del btnGuardar a "Guardar"
        deshabilitoParaEditar();
    }//GEN-LAST:event_btnResetearFiltroActionPerformed

	
	
	/** al hacer clik en una fila de la tabla, queda seleccionado una producto.
	 * Entonces habilita los botones de eliminar y modificar
	 * @param evt 
	 */
    private void tablaProductosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaProductosMouseClicked
		int numfila = tablaProductos.getSelectedRow();
		if (numfila != -1) {			
			btnEliminar.setEnabled(true); // habilito el botón de eliminar
			btnModificar.setEnabled(true); // habilito el botón de modificar
			
			filaTabla2Campos(numfila); // cargo los campos de texto de la pantalla con datos de la fila seccionada de la tabla
		}  
    }//GEN-LAST:event_tablaProductosMouseClicked

	
	
    private void btnCategoriasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCategoriasActionPerformed
		CrudCategorias crudCategorias = new CrudCategorias(this); // creo un internal Frame
		crudCategorias.setVisible(true); // lo pongo visible
		escritorio.add(crudCategorias); // lo pongo en el escritorio
		escritorio.moveToFront(crudCategorias); //pongo la ventana al frente
    }//GEN-LAST:event_btnCategoriasActionPerformed

	
	
	/**
	 * este método es llamado por el crudCategorias cuando cierra el mismo
	 */
	public void retornandoDeCrudCategorias(){
		cargarMapaCategorias(); //por si se modificaron
		cargarMapaServicios();
		cargarListaProductos();
		cargarTabla();
	}
//================================================================================
//================================================================================
	
		
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel botonera;
    private javax.swing.JButton btnAgregar;
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnCategorias;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnModificar;
    private javax.swing.JButton btnResetearFiltro;
    private javax.swing.JButton btnSalir;
    private javax.swing.ButtonGroup btngrpEstado;
    private javax.swing.JComboBox<Categoria> cbCategoria;
    private javax.swing.JComboBox<Servicio> cbDespachadoPor;
    private javax.swing.JComboBox<String> cboxOrden;
    private javax.swing.JCheckBox ckbDisponible;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblTituloTabla;
    private javax.swing.JPanel panelCamposMesa;
    private javax.swing.JPanel panelTabla;
    private javax.swing.JTable tablaProductos;
    private javax.swing.JTextField txtDescripcion;
    private javax.swing.JTextField txtIdProducto;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtPrecio;
    private javax.swing.JTextField txtStock;
    // End of variables declaration//GEN-END:variables
} // CrudProductos



//================================================================================
//================================================================================
	


/**
 * Es una clase para agrupar y almacenar los datos con los que se filtra una búsqueda
 * @author John David Molina Velarde, Leticia Mores
 */
class FiltroProductos{
	int idProducto;
	String nombre;
	int categoria;
	int despachadoPor;
	boolean estoyFiltrando;

	public FiltroProductos() { // constructor
		idProducto = -1;
		nombre = null;
		categoria = -1;
		despachadoPor = -1;
		estoyFiltrando = false;
	} // constructor FiltroProductos
} //FiltroProductos