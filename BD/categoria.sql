CREATE TABLE `categoria` (
  `idcategoria` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(45) DEFAULT NULL COMMENT 'Nombre de la categoría a la que pertenece un producto. Ej: Sopas, Carnes, Pescados, Postres, Bebidas, Entradas.',
  PRIMARY KEY (`idcategoria`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8

/*
-- Query: SELECT * FROM restaurante.categoria
LIMIT 0, 5000

-- Date: 2023-10-18 16:33
*/
INSERT INTO `categoria` (`idcategoria`,`nombre`) VALUES (1,'Bebidas');
INSERT INTO `categoria` (`idcategoria`,`nombre`) VALUES (2,'Pizzas');
INSERT INTO `categoria` (`idcategoria`,`nombre`) VALUES (3,'Sandwiches');
INSERT INTO `categoria` (`idcategoria`,`nombre`) VALUES (4,'Tacos');
INSERT INTO `categoria` (`idcategoria`,`nombre`) VALUES (5,'Postres');
