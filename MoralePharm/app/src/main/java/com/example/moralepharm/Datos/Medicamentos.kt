package com.example.moralepharm.Datos

class Medicamentos {
    var nombre: String? = null
    var precio: Double? = null
    var key: String? = null

    constructor() {}

    constructor(precio: Double?, nombre: String?) {
        this.precio = precio
        this.nombre = nombre
    }

    fun toMap(): Map<String, Any?> {
        return mapOf(
            "precio" to precio,
            "nombre" to nombre,
            "key" to key
        )
    }
}
