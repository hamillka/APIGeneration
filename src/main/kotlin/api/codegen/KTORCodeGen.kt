package api.codegen

import api.codegen.writers.*
import api.generic.GenericObject

class KTORCodeGen(genPath: String = ".") : AbstractCodeGen(genPath) {

    override fun generateCode(objects: MutableList<GenericObject>): Boolean {
        if (objects.size == 0) {
            return false
        }
        if (!generateApplication()) return false
        if (!generateRoutes(objects)) return false
        if (!generatePlugins(objects)) return false
       return generateModels(objects)
    }

    private fun generateApplication(): Boolean {
        val gen = AppClassGenerator(path)
        return gen.generate()
    }

    private fun generateRoutes(objects: MutableList<GenericObject>): Boolean {
        val gen = RouteClassGenerator(path, objects[0])
        return gen.generate()
    }

    private fun generatePlugins(objects: MutableList<GenericObject>): Boolean {
        val gen1 = RoutingClassGenerator(path, objects[0])
        val gen2 = SerializationClassGenerator(path)
        return gen1.generate() && gen2.generate()
    }

    private fun generateModels(objects: MutableList<GenericObject>): Boolean {
        val gen = ModelClassGenerator(path, objects)
        return gen.generate()
    }
}
