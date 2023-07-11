package api.codegen

import api.codegen.writers.*
import api.generic.GenericObject

class KTORCodeGen(genPath: String = ".") : AbstractCodeGen(genPath) {

    override fun generateCode(objects: MutableList<GenericObject>): Boolean {
        if (!generateApplication()) return false
        if (!generateRoutes()) return false
        if (!generatePlugins()) return false
       return generateModels()
    }

    private fun generateApplication(): Boolean {
        val gen = AppClassGenerator(path)
        return gen.generate()
    }

    private fun generateRoutes(): Boolean {
        val gen = RouteClassGenerator(path)
        return gen.generate()
    }

    private fun generatePlugins(): Boolean {
        val gen1 = RoutingClassGenerator(path)
        val gen2 = SerializationClassGenerator(path)
        return gen1.generate() && gen2.generate()
    }

    private fun generateModels(): Boolean {
        val gen = ModelClassGenerator(path)
        return gen.generate()
    }
}