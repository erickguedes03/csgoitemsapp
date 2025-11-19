package br.com.guedes.csgoitemsapp.data

object DataCache {
    // keep only agents for now to avoid unused warnings
    private val agents = mutableListOf<Any?>()

    fun <T> putAgents(list: List<T>) {
        synchronized(agents) {
            agents.clear()
            // store as Any? to avoid generic array issues
            agents.addAll(list.map { it as Any? })
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getAgents(): List<T> {
        return synchronized(agents) { agents.map { it as T } }
    }

    fun hasAgents(): Boolean = agents.isNotEmpty()
}
