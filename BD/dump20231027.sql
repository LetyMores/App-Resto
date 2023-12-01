CREATE DATABASE  IF NOT EXISTS `restaurante` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `restaurante`;
-- MySQL dump 10.13  Distrib 5.7.17, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: restaurante
-- ------------------------------------------------------
-- Server version	5.7.21-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `categoria`
--

DROP TABLE IF EXISTS `categoria`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `categoria` (
  `idcategoria` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(45) DEFAULT NULL COMMENT 'Nombre de la categoría a la que pertenece un producto. Ej: Sopas, Carnes, Pescados, Postres, Bebidas, Entradas.',
  PRIMARY KEY (`idcategoria`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categoria`
--

LOCK TABLES `categoria` WRITE;
/*!40000 ALTER TABLE `categoria` DISABLE KEYS */;
INSERT INTO `categoria` VALUES (1,'Bebidas'),(2,'Pizzas'),(3,'Sandwiches'),(4,'Tacos'),(5,'Postres');
/*!40000 ALTER TABLE `categoria` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `item`
--

DROP TABLE IF EXISTS `item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `item` (
  `idItem` int(11) NOT NULL AUTO_INCREMENT,
  `idProducto` int(11) NOT NULL COMMENT 'id del producto de este item',
  `cantidad` int(11) NOT NULL COMMENT 'Cantidad a servir',
  `idPedido` int(11) NOT NULL COMMENT 'Pedido al que pertenece el item',
  `estado` varchar(1) DEFAULT NULL COMMENT 'Estado del item: A anotado, S solicitado, D despachado, E entregado, C cancelado, V cancelado y visto.\nCuando el mozo toma nota del pedido del cliente, que como Anotado, luego el mozo solicita ese producto a la cocina o bar quedando como Solicitado, cuando la cocina o bar lo despacha queda Despachado, y cuando el mozo lo sirve queda como Entregado. Si en algún momento se cancela el pedido, el item queda como Cancelado.',
  PRIMARY KEY (`idItem`),
  KEY `idPedido` (`idPedido`),
  KEY `idProducto` (`idProducto`),
  CONSTRAINT `item_ibfk_1` FOREIGN KEY (`idPedido`) REFERENCES `pedido` (`idPedido`),
  CONSTRAINT `item_ibfk_2` FOREIGN KEY (`idProducto`) REFERENCES `producto` (`idproducto`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=94 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `item`
--

LOCK TABLES `item` WRITE;
/*!40000 ALTER TABLE `item` DISABLE KEYS */;
INSERT INTO `item` VALUES (3,5,5,2,'A'),(4,10,19,1,'D'),(5,13,1,2,'D'),(6,7,1,4,'E'),(9,17,3,2,'A'),(10,16,3,2,'D'),(11,16,15,4,'A'),(12,6,1,2,'D'),(13,15,2,2,'E'),(15,30,1,2,'A'),(16,32,1,2,'S'),(19,9,1,2,'A'),(20,13,2,2,'A'),(21,38,2,5,'S'),(22,5,1,5,'D'),(23,15,2,5,'D'),(24,42,1,5,'S'),(29,38,1,5,'A'),(30,13,1,2,'V'),(31,42,1,5,'C'),(32,26,1,6,'V'),(35,26,1,6,'V'),(38,18,2,7,'E'),(39,2,1,7,'E'),(40,39,1,7,'E'),(41,11,2,13,'E'),(43,14,1,13,'V'),(44,39,1,13,'E'),(45,39,1,13,'E'),(50,7,1,15,'V'),(53,7,1,15,'V'),(54,5,3,15,'A'),(55,6,1,15,'V'),(56,10,1,15,'A'),(57,12,1,15,'E'),(58,6,2,15,'A'),(59,21,2,16,'E'),(60,30,1,16,'V'),(61,42,1,16,'A'),(62,2,1,16,'V'),(63,3,1,16,'V'),(64,6,1,16,'V'),(65,8,2,16,'D'),(66,3,2,16,'V'),(67,11,1,16,'V'),(68,27,1,16,'D'),(69,27,1,16,'V'),(70,31,1,16,'V'),(71,28,1,16,'V'),(72,2,1,17,'V'),(73,4,1,17,'V'),(74,6,1,17,'D'),(75,4,1,17,'V'),(76,5,2,17,'D'),(77,7,1,17,'V'),(78,30,1,16,'D'),(79,11,1,17,'D'),(80,3,1,17,'E'),(81,31,1,18,'E'),(82,4,1,18,'E'),(83,6,2,18,'E'),(84,7,1,18,'E'),(86,12,1,18,'V'),(87,3,1,21,'E'),(88,11,1,18,'V'),(89,12,1,18,'V'),(90,11,1,18,'V'),(91,12,1,18,'V'),(92,44,1,17,'E'),(93,44,1,17,'C');
/*!40000 ALTER TABLE `item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mesa`
--

DROP TABLE IF EXISTS `mesa`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mesa` (
  `idMesa` int(11) NOT NULL AUTO_INCREMENT,
  `capacidad` int(11) NOT NULL COMMENT 'Cuantas personas admite la mesa',
  `estado` varchar(1) NOT NULL COMMENT 'Estado de la mesa: L libre, O ocupada, A atendida.\n',
  `idmesero` int(11) DEFAULT NULL COMMENT 'Es el mesero que está atendiendo la mesa. Corresponde al idServicio que sea tipo M\n',
  PRIMARY KEY (`idMesa`),
  KEY `mesa_fk_1_idx` (`idmesero`),
  CONSTRAINT `mesa_fk_1` FOREIGN KEY (`idmesero`) REFERENCES `servicio` (`idservicio`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mesa`
--

LOCK TABLES `mesa` WRITE;
/*!40000 ALTER TABLE `mesa` DISABLE KEYS */;
INSERT INTO `mesa` VALUES (1,4,'L',6),(2,6,'A',8),(3,1,'L',8),(4,2,'A',6),(5,7,'A',5),(6,5,'A',5),(7,4,'L',5),(8,2,'L',NULL),(9,10,'L',5),(10,6,'L',6),(11,4,'L',7),(12,8,'L',8),(13,5,'L',NULL),(27,5,'L',8),(28,3,'L',8),(29,6,'L',6);
/*!40000 ALTER TABLE `mesa` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pedido`
--

DROP TABLE IF EXISTS `pedido`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pedido` (
  `idPedido` int(11) NOT NULL AUTO_INCREMENT,
  `idMesa` int(11) NOT NULL,
  `idMesero` int(11) NOT NULL,
  `fechaHora` datetime NOT NULL,
  `estado` varchar(1) NOT NULL DEFAULT 'A' COMMENT 'Estado del pedido: A activo, P pagado, C canceado',
  PRIMARY KEY (`idPedido`),
  KEY `idMesa` (`idMesa`),
  KEY `pedido_ibfk_1_idx` (`idMesero`),
  CONSTRAINT `pedido_ibfk_1` FOREIGN KEY (`idMesero`) REFERENCES `servicio` (`idservicio`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `pedido_ibfk_2` FOREIGN KEY (`idMesa`) REFERENCES `mesa` (`idMesa`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pedido`
--

LOCK TABLES `pedido` WRITE;
/*!40000 ALTER TABLE `pedido` DISABLE KEYS */;
INSERT INTO `pedido` VALUES (1,1,6,'2023-09-01 10:25:00','A'),(2,6,5,'2023-10-13 19:01:00','A'),(3,3,6,'2023-10-05 17:23:54','C'),(4,1,7,'2023-08-22 07:15:35','A'),(5,5,5,'2023-10-13 17:34:00','A'),(6,6,5,'2023-10-17 10:46:54','C'),(7,2,8,'2023-10-17 11:51:20','P'),(8,3,8,'2023-10-17 14:39:40','C'),(9,3,8,'2023-10-17 14:40:04','P'),(10,3,8,'2023-10-17 14:50:47','C'),(11,3,8,'2023-10-17 15:00:19','C'),(12,2,8,'2023-10-17 15:00:30','P'),(13,6,5,'2023-10-17 15:34:55','P'),(14,6,5,'2023-10-17 15:40:41','C'),(15,6,5,'2023-10-17 15:41:40','A'),(16,2,8,'2023-10-19 17:15:14','A'),(17,2,8,'2023-10-20 16:51:07','A'),(18,3,8,'2023-10-25 16:00:35','P'),(19,27,8,'2023-10-26 16:42:15','C'),(20,27,8,'2023-10-26 16:49:40','C'),(21,27,8,'2023-10-27 13:10:57','P');
/*!40000 ALTER TABLE `pedido` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `producto`
--

DROP TABLE IF EXISTS `producto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `producto` (
  `idproducto` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(30) NOT NULL COMMENT 'Nombre del plato o producto',
  `descripcion` varchar(120) NOT NULL COMMENT 'Descripcion mas detallada del producto',
  `stock` int(11) NOT NULL,
  `precio` double NOT NULL,
  `disponible` tinyint(4) DEFAULT NULL COMMENT 'Verdadero si ese producto está en la carta.',
  `idcategoria` int(11) DEFAULT NULL COMMENT 'Categoria a la que pertenece el producto: Sopas, carnes, pastas, pescados, bebidas, etc.',
  `despachadopor` int(11) DEFAULT NULL COMMENT 'idServicio del servicio que se encarga de despachar este producto (por ejemplo, un pollo con papas lo prepara y despacha la cocina, un licuado lo prepara y despacha el bar). Si es null, ningun servicio lo despacha, sino que el mozo lo agarra directamente (por ejemplo toma una gaseosa de la heladera)',
  PRIMARY KEY (`idproducto`),
  KEY `producto_ibfk_idx` (`idcategoria`),
  KEY `producto_ibfk_2_idx` (`despachadopor`),
  CONSTRAINT `producto_ibfk` FOREIGN KEY (`idcategoria`) REFERENCES `categoria` (`idcategoria`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `producto_ibfk_2` FOREIGN KEY (`despachadopor`) REFERENCES `servicio` (`idservicio`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=46 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `producto`
--

LOCK TABLES `producto` WRITE;
/*!40000 ALTER TABLE `producto` DISABLE KEYS */;
INSERT INTO `producto` VALUES (1,'Pizza Jamón y Morrones','Mozzarella, Jamón y Morrones asados ',40,4500,1,2,2),(2,'Pizza Mozzarela','Mozzarella, salsa de tomate',50,4500,1,3,2),(3,'Pizza Margarita','Mozzarella, tomate y albahaca',50,4500,1,2,2),(4,'Pizza Napolitana','Mozzarella, tomate, ajo y aceitunas',40,4800,1,2,2),(5,'Pizza Calabresa','Mozzarella, salsa de tomate y longaniza',45,5000,1,2,2),(6,'Pizza 4 Quesos','Mozzarella, Provolone, Chedar y Queso Azul',40,6500,1,2,2),(7,'Pizza Rúcula','Mozzarella, rúcula, jamón crudo y parmesano',35,5700,1,2,2),(8,'Pizza Hawaiana','Mozzarella, tomate, jamón y anana',35,5200,1,2,2),(9,'Pizza Vegetariana','Mozzarella, tomate, champiñones y pimientos',30,4900,1,2,2),(10,'Sandwich de Jamón y Queso','Jamón, queso, lechuga y tomate',60,2800,1,3,2),(11,'Sandwich de Pollo','Pechuga de pollo a la parrilla, lechuga, tomate y mayonesa',55,3200,1,3,2),(12,'Sandwich de Lomito','Lomito de res, lechuga, tomate, cebolla y mayonesa',50,3000,1,3,2),(13,'Sandwich de Lomito Completo','Lomito de res, lechuga, tomate, huevo y queso chedar',40,4000,1,3,2),(14,'Sándwich BLT','Sándwich de bacon, lechuga y tomate',37,2700,1,3,2),(15,'Sandwich de Vegetales','Vegetales asados, mozzarella y aderezo especial',40,3300,1,3,2),(16,'Sandwich de Milanesa','Milanesa de ternera, lechuga, tomate y mayonesa',45,4200,1,3,2),(17,'Sandwich de Suprema','Milanesa de pollo, lechuga, tomate y mayonesa',45,4200,1,3,2),(18,'Taco de Carne','Carne de res, cebolla, cilantro y salsa de tomate',40,3500,1,4,2),(19,'Taco de Cerdo','Bondiola desmechada, cebolla caramelizada, y BBQ',40,3555,1,4,2),(20,'Tacos de Carne Asada','Tacos de carne asada con cebolla y cilantro',25,2500,1,4,2),(21,'Taco de Pollo Clásico','Pollo desmenuzado, cebolla, lechuga y crema agria',35,3160,1,4,2),(22,'Tacos de Pollo BBQ','Tacos de pollo a la barbacoa con cebolla morada',38,3300,1,4,2),(23,'Taco de Pescado','Filet de pescado empanizado, repollo y salsa de chipotle',30,2500,1,4,2),(24,'Taco de Camarones','Camarones salteados, aguacate, cilantro y salsa picante',30,4220,1,4,2),(25,'Taco Vegetariano','Vegetales a la parrilla, palta y salsa de cilantro',25,2890,1,4,2),(26,'Tacos de Tofu','Tacos de tofu con guacamole',28,3300,1,4,2),(27,'Hamburguesa Clásica','Carne de res, lechuga, tomate, cebolla y salsa especial',50,3260,1,3,2),(28,'Hamburguesa con queso','Hamburguesa con carne de res, lechuga, tomate, cebolla y queso cheddar',45,4200,1,3,2),(29,'Hamburguesa de Pollo','Hamburguesa de pollo, lechuga, tomate y mayonesa',45,3280,1,3,2),(30,'Hamburguesa BBQ','Carne de res, cebolla caramelizada, bacon y salsa barbacoa',55,3300,1,3,2),(31,'Hamburguesa Vegetariana','Hamburguesa de lentejas, lechuga, tomate y salsa de palta',40,3250,1,3,2),(32,'Tarta de Manzana','Tarta de manzana con canela y crema',20,2150,1,5,10),(33,'Helado de Chocolate','Helado de chocolate con nueces y salsa de caramelo',15,2120,1,5,10),(34,'Flan Casero','Flan casero con caramelo',25,2100,1,5,10),(35,'Mousse de Frutilla','Mousse de frutilla con frutas frescas',20,2180,1,5,10),(36,'Cheesecake de Oreo','Cheesecake de oreo con crema batida',18,2200,1,5,10),(37,'Gaseosa Coca-Cola','Refresco de cola, lata 355ml',80,600,1,1,3),(38,'Gaseosa Sprite','Refresco de lima-limón, lata 355ml',60,600,1,1,3),(39,'Cerveza Quilmes 1L','Cerveza rubia, botella 355ml',40,1120,1,1,3),(40,'Cerveza Patagonia','Cerveza artesanal, botella 355ml',35,1150,1,1,3),(41,'Agua Mineral','Agua mineral sin gas, botella 500ml',90,660,1,1,3),(42,'Coca Cola Light',' Gaseosa Coca Cola Light Sin azucar 1L',12,1200.5,1,1,3),(43,'Crush Lima Limón',' Gaseosa Crush Sabor lima Limón si azúcar 2.25 L',20,400.5,1,1,3),(44,'Agua corriente',' Agua de la canilla',30,0,1,1,NULL),(45,'Prueba','Producto de prueba',40,0,1,1,NULL);
/*!40000 ALTER TABLE `producto` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `servicio`
--

DROP TABLE IF EXISTS `servicio`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `servicio` (
  `idservicio` int(11) NOT NULL AUTO_INCREMENT,
  `nombreServicio` varchar(45) NOT NULL COMMENT 'Nombre del servicio o mozo, ej: Administracion, Cocina, Bar, Mesero Juan, Mesera Ana',
  `host` varchar(45) NOT NULL COMMENT 'Host de la máquina.',
  `puerto` int(11) NOT NULL COMMENT 'puerto en el que escucha esta máquina',
  `tipo` varchar(1) NOT NULL DEFAULT 'M' COMMENT 'Es el tipo de servicio que se presta: A administracion, M mesero, S servicio (cocina, bar, etc), R recepcionista',
  `clave` varchar(30) DEFAULT NULL COMMENT 'La clave para loguerarse al servicio. Puede estar en blanco para ingresar sin clave',
  PRIMARY KEY (`idservicio`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `servicio`
--

LOCK TABLES `servicio` WRITE;
/*!40000 ALTER TABLE `servicio` DISABLE KEYS */;
INSERT INTO `servicio` VALUES (1,'Administración','localhost',20000,'A','12345'),(2,'Cocina','localhost',20001,'S',NULL),(3,'Bar','localhost',20002,'S',NULL),(4,'Recepcion','localhost',20003,'R',NULL),(5,'Leticia Mores','localhost',20004,'M','12345'),(6,'Enrique Martinez','localhost',20005,'M','12345'),(7,'Eduardo Beltran','localhost',20006,'M','12345'),(8,'John David Molina','localhost',20007,'M','12345'),(9,'Jorge González','localhost',20008,'R','12345'),(10,'Cafeteria','localhost',20009,'S',NULL);
/*!40000 ALTER TABLE `servicio` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2023-10-27 14:27:30
