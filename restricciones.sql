SELECT * FROM restaurante.item;

-- Deshabilitar restricciones de clave foranea
-- ALTER TABLE estudiante NOCHECK CONSTRAINT fk_estudiante_carrera;
-- Para volver a activar la restricción
-- ALTER TABLE estudiante CHECK CONSTRAINT fk_estudiante_carrera;


-- 
-- Para desactivar todas las restricciones
-- EXEC sp_msforeachtable "ALTER TABLE ? NOCHECK CONSTRAINT ALL";
-- Para volver a activar todas las restriciones
-- EXEC sp_msforeachtable "ALTER TABLE ? WITH CHECK CHECK CONSTRAINT ALL";

UPDATE restaurante.item SET idProducto=(idProducto-42);