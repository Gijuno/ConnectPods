package us.gijuno.connectpods

data class StatusElement(
    var charge: Byte = 0,
    var charging: Boolean = false,
    var connected: Boolean = false
) : Cloneable {

    fun createClone(): StatusElement = clone() as StatusElement
}