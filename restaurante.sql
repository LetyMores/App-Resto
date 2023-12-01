-- creo la B.D.
CREATE SCHEMA `restaurante` ;

-- creo la tabla producto
CREATE TABLE `restaurante`.`producto` (
  `idproducto` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(30) NOT NULL,
  `descripcion` VARCHAR(120) NOT NULL,
  `stock` INT(11) NOT NULL,
  `precio` DOUBLE NOT NULL,
  PRIMARY KEY (`idproducto`));

CREATE TABLE `restaurante`.`mesero` (
  `idmesero` INT NOT NULL,
  `nombreCompleto` VARCHAR(50) NOT NULL,
  `clave` VARCHAR(30) NOT NULL,
  PRIMARY KEY (`idmesero`));
  
 --
-- Estructura de tabla para la tabla `items`
--

CREATE TABLE `items` (
  `idItem` int(11) NOT NULL,
  `idProducto` int(11) NOT NULL,
  `cantidad` int(11) NOT NULL,
  `idPedido` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `mesa`
--

CREATE TABLE `mesa` (
  `idMesa` int(11) NOT NULL,
  `capacidad` int(11) NOT NULL,
  `estado` varchar(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `mesero`
--

CREATE TABLE `mesero` (
  `idMesero` int(11) NOT NULL,
  `nombreCompleto` varchar(50) NOT NULL,
  `clave` varchar(30) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `pedido`
--

CREATE TABLE `pedido` (
  `idPedido` int(11) NOT NULL,
  `idMesa` int(11) NOT NULL,
  `idMesero` int(11) NOT NULL,
  `pagado` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `producto`
--

CREATE TABLE `producto` (
  `idProducto` int(11) NOT NULL,
  `nombre` varchar(30) NOT NULL,
  `descripcion` varchar(120) NOT NULL,
  `stock` int(11) NOT NULL,
  `precio` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `producto`
--

INSERT INTO `producto` (`idProducto`, `nombre`, `descripcion`, `stock`, `precio`) VALUES
(1, 'Lomito completo', 'Lomito  con lechuga, tomate, huevo y queso chedar', 50, 2500);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `reserva`
--

CREATE TABLE `reserva` (
  `idReserva` int(11) NOT NULL,
  `nombreCliente` varchar(30) NOT NULL,
  `dni` int(11) NOT NULL,
  `fecha` date NOT NULL,
  `hora` time NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `items`
--
ALTER TABLE `items`
  ADD PRIMARY KEY (`idItem`),
  ADD KEY `idPedido` (`idPedido`),
  ADD KEY `idProducto` (`idProducto`);

--
-- Indices de la tabla `mesa`
--
ALTER TABLE `mesa`
  ADD PRIMARY KEY (`idMesa`);

--
-- Indices de la tabla `mesero`
--
ALTER TABLE `mesero`
  ADD PRIMARY KEY (`idMesero`);

--
-- Indices de la tabla `pedido`
--
ALTER TABLE `pedido`
  ADD PRIMARY KEY (`idPedido`),
  ADD KEY `idMesero` (`idMesero`),
  ADD KEY `idMesa` (`idMesa`);

--
-- Indices de la tabla `producto`
--
ALTER TABLE `producto`
  ADD PRIMARY KEY (`idProducto`);

--
-- Indices de la tabla `reserva`
--
ALTER TABLE `reserva`
  ADD PRIMARY KEY (`idReserva`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `items`
--
ALTER TABLE `items`
  MODIFY `idItem` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `mesa`
--
ALTER TABLE `mesa`
  MODIFY `idMesa` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `mesero`
--
ALTER TABLE `mesero`
  MODIFY `idMesero` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `pedido`
--
ALTER TABLE `pedido`
  MODIFY `idPedido` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `producto`
--
ALTER TABLE `producto`
  MODIFY `idProducto` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `reserva`
--
ALTER TABLE `reserva`
  MODIFY `idReserva` int(11) NOT NULL AUTO_INCREMENT;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `items`
--
ALTER TABLE `items`
  ADD CONSTRAINT `items_ibfk_1` FOREIGN KEY (`idPedido`) REFERENCES `pedido` (`idPedido`),
  ADD CONSTRAINT `items_ibfk_2` FOREIGN KEY (`idProducto`) REFERENCES `producto` (`idProducto`);

--
-- Filtros para la tabla `pedido`
--
ALTER TABLE `pedido`
  ADD CONSTRAINT `pedido_ibfk_1` FOREIGN KEY (`idMesero`) REFERENCES `mesero` (`idMesero`),
  ADD CONSTRAINT `pedido_ibfk_2` FOREIGN KEY (`idMesa`) REFERENCES `mesa` (`idMesa`);

--
-- Filtros para la tabla `reserva`
--
ALTER TABLE `reserva`
  ADD CONSTRAINT `reserva_ibfk_1` FOREIGN KEY (`idReserva`) REFERENCES `mesa` (`idMesa`);
COMMIT;
 
 
 -- concedo todos los privilegios de la bd gestion universidad al usuario
GRANT ALL PRIVILEGES ON `restaurante` . * TO 'john'@'localhost';

-- refrescar los privilegios
FLUSH PRIVILEGES;


-- ****************************************************************************************
-- ****************************************************************************************

INSERT INTO `mesa`(`capacidad`, `estado`) VALUES (4, 'L'), (6, 'L'), (8 , 'L'), 
(2 , 'L'), (4 , 'L'), (5, 'L'), (4, 'L'), (2, 'L'), (10, 'L'), (6, 'L'), (4, 'L'), 
(8, 'L'), (5, 'L'), (3, 'L'), (6,'L'), (4,'L'), (8,'L'), (4,'L'), (2,'L'), (2,'L'), 
(4,'L'), (6,'L'), (12,'L');

INSERT INTO `producto` (`idProducto`, `nombre`, `descripcion`, `stock`, `precio`) 
VALUES (NULL, 'Pizza Jamón y Morrones', 'Mozzarella, Jamón y Morrones asados ', '40', '4500'), 
(NULL, 'Pizza Mozzarela', 'Mozzarella, salsa de tomate', '50', '4500'), 
(NULL, 'Pizza Margarita', 'Mozzarella, tomate y albahaca', '50', '4500'), 
(NULL, 'Pizza Napolitana', 'Mozzarella, tomate, ajo y aceitunas', '40', '4800'), 
(NULL, 'Pizza Calabresa', 'Mozzarella, salsa de tomate y longaniza', '45', '5000'), 
(NULL, 'Pizza 4 Quesos', 'Mozzarella, Provolone, Chedar y Queso Azul', '40', '6500'), 
(NULL, 'Pizza Rúcula', 'Mozzarella, rúcula, jamón crudo y parmesano', '35', '5700'), 
(NULL, 'Pizza Hawaiana', 'Mozzarella, tomate, jamón y anana', '35', '5200'), 
(NULL, 'Pizza Vegetariana', 'Mozzarella, tomate, champiñones y pimientos', '30', '4900'), 
(NULL, 'Sandwich de Jamón y Queso', 'Jamón, queso, lechuga y tomate', '60', '2800'), 
(NULL, 'Sandwich de Pollo', 'Pechuga de pollo a la parrilla, lechuga, tomate y mayonesa', '55', '3200'), 
(NULL, 'Sandwich de Lomito', 'Lomito de res, lechuga, tomate, cebolla y mayonesa', '50', '3000'), 
(NULL, 'Sandwich de Lomito Completo', 'Lomito de res, lechuga, tomate, huevo y queso chedar', '40', '4000'), 
(NULL, 'Sándwich BLT', 'Sándwich de bacon, lechuga y tomate', '37', '2700'), 
(NULL, 'Sandwich de Vegetales', 'Vegetales asados, mozzarella y aderezo especial', '40', '3300'), 
(NULL, 'Sandwich de Milanesa', 'Milanesa de ternera, lechuga, tomate y mayonesa', '45', '4200'), 
(NULL, 'Sandwich de Suprema', 'Milanesa de pollo, lechuga, tomate y mayonesa', '45', '4200'), 
(NULL, 'Taco de Carne', 'Carne de res, cebolla, cilantro y salsa de tomate', '40', '3500'), 
(NULL, 'Taco de Cerdo', 'Bondiola desmechada, cebolla caramelizada, y BBQ', '40', '3500'), 
(NULL, 'Tacos de Carne Asada', 'Tacos de carne asada con cebolla y cilantro', '25', '2500'), 
(NULL, 'Taco de Pollo Clásico', 'Pollo desmenuzado, cebolla, lechuga y crema agria', '35', '3160'), 
(NULL, 'Tacos de Pollo BBQ', 'Tacos de pollo a la barbacoa con cebolla morada', '38', '3300'), 
(NULL, 'Taco de Pescado', 'Filet de pescado empanizado, repollo y salsa de chipotle', '30', '2500'), 
(NULL, 'Taco de Camarones', 'Camarones salteados, aguacate, cilantro y salsa picante', '30', '4220'), 
(NULL, 'Taco Vegetariano', 'Vegetales a la parrilla, palta y salsa de cilantro', '25', '2890'), 
(NULL, 'Tacos de Tofu', 'Tacos de tofu con guacamole', '28', '3300'), 
(NULL, 'Hamburguesa Clásica', 'Carne de res, lechuga, tomate, cebolla y salsa especial', '50', '3260'), 
(NULL, 'Hamburguesa con queso', 'Hamburguesa con carne de res, lechuga, tomate, cebolla y queso cheddar', '45', '4200'), 
(NULL, 'Hamburguesa de Pollo', 'Hamburguesa de pollo, lechuga, tomate y mayonesa', '45', '3280'), 
(NULL, 'Hamburguesa BBQ', 'Carne de res, cebolla caramelizada, bacon y salsa barbacoa', '55', '3300'), 
(NULL, 'Hamburguesa Vegetariana', 'Hamburguesa de lentejas, lechuga, tomate y salsa de palta', '40', '3250'), 
(NULL, 'Tarta de Manzana', 'Tarta de manzana con canela y crema', '20', '2150'), 
(NULL, 'Helado de Chocolate', 'Helado de chocolate con nueces y salsa de caramelo', '15', '2120'), 
(NULL, 'Flan Casero', 'Flan casero con caramelo', '25', '2100'), 
(NULL, 'Mousse de Frutilla', 'Mousse de frutilla con frutas frescas', '20', '2180'), 
(NULL, 'Cheesecake de Oreo', 'Cheesecake de oreo con crema batida', '18', '2200'), 
(NULL, 'Gaseosa Coca-Cola', 'Refresco de cola, lata 355ml', '80', '600'), 
(NULL, 'Gaseosa Sprite', 'Refresco de lima-limón, lata 355ml', '60', '600'), 
(NULL, 'Cerveza Quilmes', 'Cerveza rubia, botella 355ml', '40', '1120'), 
(NULL, 'Cerveza Patagonia', 'Cerveza artesanal, botella 355ml', '35', '1150'), 
(NULL, 'Agua Mineral', 'Agua mineral sin gas, botella 500ml', '90', '660');

delete from producto;


update restaurante.producto set disponible = True;

ALTER TABLE `restaurante`.`items` 
RENAME TO  `restaurante`.`item` ;

INSERT INTO `mesero` (`idMesero`, `nombreCompleto`, `clave`) VALUES 
(NULL, 'Leticia Mores', '12345'), 
(NULL, 'Enrique Martinez', '23456'), 
(NULL, 'John Molina Velarde', '34567'), 
(NULL, 'Eduardo Beltran', '45678');


-- cambios desde el 4/10/23

ALTER TABLE `restaurante`.`item` 
ADD COLUMN `estado` VARCHAR(1) NULL AFTER `idPedido`;

update item set estado = 'A';

ALTER TABLE `restaurante`.`reserva` 
DROP COLUMN `hora`,
CHANGE COLUMN `fecha` `fechahora` DATETIME NOT NULL ;

ALTER TABLE `restaurante`.`pedido` 
ADD COLUMN `fechaHora` DATETIME NOT NULL AFTER `idMesero`;


-- cambios desde el 6/10/23

CREATE TABLE `restaurante`.`categoria` (
  `idcategoria` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(45) NULL,
  PRIMARY KEY (`idcategoria`));
ALTER TABLE `restaurante`.`producto` 
ADD COLUMN `idcategoria` INT NULL AFTER `disponible`,
ADD INDEX `producto_ibfk_idx` (`idcategoria` ASC);
ALTER TABLE `restaurante`.`producto` 
ADD CONSTRAINT `producto_ibfk`
  FOREIGN KEY (`idcategoria`)
  REFERENCES `restaurante`.`categoria` (`idcategoria`)
  ON DELETE SET NULL
  ON UPDATE CASCADE;

CREATE TABLE `restaurante`.`servicio` (
  `idservicio` INT NOT NULL AUTO_INCREMENT,
  `nombreServicio` VARCHAR(45) NOT NULL,
  `host` VARCHAR(45) NOT NULL,
  `puerto` INT NOT NULL,
  PRIMARY KEY (`idservicio`));


INSERT INTO `categoria`(`nombre`) VALUES 
('Pizzas'),
('Sandwiches'),
('Tacos'),
('Hamburguesas'),
('Postres'),
('Bebidas');


ALTER TABLE `restaurante`.`producto` 
ADD COLUMN `idservicio` INT NULL AFTER `idcategoria`;

ALTER TABLE `restaurante`.`mesa` 
ADD COLUMN `idmesero` INT NULL AFTER `estado`;


-- cambios desde el 7/10/23

ALTER TABLE `restaurante`.`servicio` 
ADD COLUMN `tipo` VARCHAR(1) NOT NULL DEFAULT 'M' AFTER `puerto`,
ADD COLUMN `clave` VARCHAR(30) NULL AFTER `tipo`;

ALTER TABLE `restaurante`.`servicio` 
CHANGE COLUMN `nombreServicio` `nombreServicio` VARCHAR(45) NOT NULL COMMENT 'Nombre del servicio o mozo, ej: Administracion, Cocina, Bar, Mesero Juan, Mesera Ana' ,
CHANGE COLUMN `host` `host` VARCHAR(45) NOT NULL COMMENT 'Host de la máquina.' ,
CHANGE COLUMN `puerto` `puerto` INT(11) NOT NULL COMMENT 'puerto en el que escucha esta máquina' ,
CHANGE COLUMN `tipo` `tipo` VARCHAR(1) NOT NULL DEFAULT 'M' COMMENT 'Es el tipo de servicio que se presta: A administracion, M mesero, S servicio (cocina, bar, etc), R recepcionista' ,
CHANGE COLUMN `clave` `clave` VARCHAR(30) NULL DEFAULT NULL COMMENT 'La clave para loguerarse al servicio. Puede estar en blanco para ingresar sin clave' ;

INSERT INTO `restaurante`.`servicio` (`nombreServicio`, `host`, `puerto`, `tipo`, `clave`) VALUES 
('Administracion', 'localhost', '20000', 'A', '12345'),
('Cocina', 'localhost', '20001', 'S', null),
('Bar', 'localhost', '20002', 'S', null),
('Recepcion', 'localhost', '20003', 'R', null),
('Leticia Mores', 'localhost', '20004', 'M', '12345'),
('Enrique Martinez', 'localhost', '20005', 'M', '12345'),
('Eduardo Beltran', 'localhost', '20006', 'M', '12345'),
('John Molina', 'localhost', '20007', 'M', '12345');

ALTER TABLE `restaurante`.`pedido` 
DROP FOREIGN KEY `pedido_ibfk_1`;

ALTER TABLE `restaurante`.`pedido` 
ADD INDEX `pedido_ibfk_1_idx` (`idMesero` ASC),
DROP INDEX `idMesero` ;

ALTER TABLE `restaurante`.`pedido` 
ADD CONSTRAINT `pedido_ibfk_1`
  FOREIGN KEY (`idMesero`)
  REFERENCES `restaurante`.`servicio` (`idservicio`);

Drop table restaurante.mesero;

ALTER TABLE `restaurante`.`categoria` 
CHANGE COLUMN `nombre` `nombre` VARCHAR(45) NULL DEFAULT NULL COMMENT 'Nombre de la categoría a la que pertenece un producto. Ej: Sopas, Carnes, Pescados, Postres, Bebidas, Entradas.' ;

ALTER TABLE `restaurante`.`item` 
DROP FOREIGN KEY `item_ibfk_1`,
DROP FOREIGN KEY `item_ibfk_2`;

ALTER TABLE `restaurante`.`item` 
CHANGE COLUMN `idProducto` `idProducto` INT(11) NOT NULL COMMENT 'id del producto de este item' ,
CHANGE COLUMN `cantidad` `cantidad` INT(11) NOT NULL COMMENT 'Cantidad a servir' ,
CHANGE COLUMN `idPedido` `idPedido` INT(11) NOT NULL COMMENT 'Pedido al que pertenece el item' ,
CHANGE COLUMN `estado` `estado` VARCHAR(1) NULL DEFAULT NULL COMMENT 'Estado del item: A anotado, S solicitado, D despachado, E entregado.\nCuando el mozo toma nota del pedido del cliente, que como Anotado, luego el mozo solicita ese producto a la cocina o bar quedando como Solicitado, cuando la cocina o bar lo despacha queda Despachado, y cuando el mozo lo sirve queda como Entregado.' ;

ALTER TABLE `restaurante`.`item` 
ADD CONSTRAINT `item_ibfk_1`
  FOREIGN KEY (`idPedido`)
  REFERENCES `restaurante`.`pedido` (`idPedido`),
ADD CONSTRAINT `item_ibfk_2`
  FOREIGN KEY (`idProducto`)
  REFERENCES `restaurante`.`producto` (`idproducto`);

ALTER TABLE `restaurante`.`mesa` 
CHANGE COLUMN `capacidad` `capacidad` INT(11) NOT NULL COMMENT 'Cuantas personas admite la mesa' ,
CHANGE COLUMN `estado` `estado` VARCHAR(1) NOT NULL COMMENT 'Estado de la mesa: L libre, O ocupada, A atendida.\n' ,
CHANGE COLUMN `idmesero` `idmesero` INT(11) NULL DEFAULT NULL COMMENT 'Es el mesero que está atendiendo la mesa. Corresponde al idServicio que sea tipo M\n' ;

ALTER TABLE `restaurante`.`producto` 
DROP FOREIGN KEY `producto_ibfk`;

ALTER TABLE `restaurante`.`producto` 
CHANGE COLUMN `nombre` `nombre` VARCHAR(30) NOT NULL COMMENT 'Nombre del plato o producto' ,
CHANGE COLUMN `descripcion` `descripcion` VARCHAR(120) NOT NULL COMMENT 'Descripcion mas detallada del producto' ,
CHANGE COLUMN `disponible` `disponible` TINYINT(4) NULL DEFAULT NULL COMMENT 'Verdadero si ese producto está en la carta.' ,
CHANGE COLUMN `idcategoria` `idcategoria` INT(11) NULL DEFAULT NULL COMMENT 'Categoria a la que pertenece el producto: Sopas, carnes, pastas, pescados, bebidas, etc.' ,
CHANGE COLUMN `idservicio` `despachadopor` INT(11) NULL DEFAULT NULL COMMENT 'idServicio del servicio que se encarga de despachar este producto (por ejemplo, un pollo con papas lo prepara y despacha la cocina, un licuado lo prepara y despacha el bar). Si es null, ningun servicio lo despacha, sino que el mozo lo agarra directamente (por ejemplo toma una gaseosa de la heladera)' ;

ALTER TABLE `restaurante`.`producto` 
ADD CONSTRAINT `producto_ibfk`
  FOREIGN KEY (`idcategoria`)
  REFERENCES `restaurante`.`categoria` (`idcategoria`)
  ON DELETE SET NULL
  ON UPDATE CASCADE;

-- cambios del 27/10/23
ALTER TABLE `restaurante`.`producto` 
ADD INDEX `producto_ibfk_2_idx` (`despachadopor` ASC);
ALTER TABLE `restaurante`.`producto` 
ADD CONSTRAINT `producto_ibfk_2`
  FOREIGN KEY (`despachadopor`)
  REFERENCES `restaurante`.`servicio` (`idservicio`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE `restaurante`.`mesa` 
ADD INDEX `mesa_fk_1_idx` (`idmesero` ASC);
ALTER TABLE `restaurante`.`mesa` 
ADD CONSTRAINT `mesa_fk_1`
  FOREIGN KEY (`idmesero`)
  REFERENCES `restaurante`.`servicio` (`idservicio`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;
  
  
ALTER TABLE `restaurante`.`pedido` 
DROP FOREIGN KEY `pedido_ibfk_1`,
DROP FOREIGN KEY `pedido_ibfk_2`;
ALTER TABLE `restaurante`.`pedido` 
ADD CONSTRAINT `pedido_ibfk_1`
  FOREIGN KEY (`idMesero`)
  REFERENCES `restaurante`.`servicio` (`idservicio`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION,
ADD CONSTRAINT `pedido_ibfk_2`
  FOREIGN KEY (`idMesa`)
  REFERENCES `restaurante`.`mesa` (`idMesa`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE `restaurante`.`producto` 
DROP FOREIGN KEY `producto_ibfk`;
ALTER TABLE `restaurante`.`producto` 
ADD CONSTRAINT `producto_ibfk`
  FOREIGN KEY (`idcategoria`)
  REFERENCES `restaurante`.`categoria` (`idcategoria`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;




-- Cambios desde el 15/10/23
-- este cambio es solo para mi, que mis idProducto empiezan en 43:
ALTER TABLE `restaurante`.`item` 
DROP FOREIGN KEY `item_ibfk_2`;

UPDATE restaurante.item SET idProducto=(idProducto-42);
UPDATE restaurante.producto SET idproducto=(idProducto-42);

ALTER TABLE `restaurante`.`item` 
ADD CONSTRAINT `item_ibfk_2`
  FOREIGN KEY (`idProducto`)
  REFERENCES `restaurante`.`producto` (`idproducto`)
  ON UPDATE CASCADE;
-- fin cambios olo para mi


INSERT INTO `producto` (`idProducto`, `nombre`, `descripcion`, `stock`, `precio`) 
VALUES (NULL, 'Prueba', 'Producto de prueba', '40', '0');



ALTER TABLE `restaurante`.`item` 
CHANGE COLUMN `estado` `estado` VARCHAR(1) NULL DEFAULT NULL COMMENT 'Estado del item: A anotado, S solicitado, D despachado, E entregado, C cancelado, V cancelado y visto.\nCuando el mozo toma nota del pedido del cliente, que como Anotado, luego el mozo solicita ese producto a la cocina o bar quedando como Solicitado, cuando la cocina o bar lo despacha queda Despachado, y cuando el mozo lo sirve queda como Entregado. Si en algún momento se cancela el pedido, el item queda como Cancelado.' ;

ALTER TABLE `restaurante`.`pedido` 
CHANGE COLUMN `pagado` `estado` VARCHAR(1) NOT NULL DEFAULT 'A' COMMENT 'Estado del pedido: A activo, P pagado, C canceado' ;
