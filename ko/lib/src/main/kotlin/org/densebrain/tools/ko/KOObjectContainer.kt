package org.densebrain.tools.ko

data class KOObjectContainer(val children: MutableMap<String, Any> = mutableMapOf()) : KOLoggable {

  fun getContainer(name: String) = get(name) {
    KOObjectContainer()
  }!!

  fun set(name: String, value: Any) {
    children.set(name, value)
  }

  fun getString(name: String) = get<String>(name)

  fun getInt(name: String) = get<Int>(name)

  fun getDouble(name: String) = get<Double>(name)

  inline fun <reified T : Any> get(name: String, noinline block: (() -> T)? = null): T? {
    var value = children[name]
    if (value == null) {
      val newValue = block?.invoke()
      if (newValue != null) {
        children[name] = newValue
        value = newValue
      }
    }

    require(value == null || value is T?)
    return value as T?
  }

  @Suppress("UNCHECKED_CAST")
  val containers
    get() = children.filter { (_, value) -> value is KOObjectContainer } as Map<String, KOObjectContainer>

  val values
    get() = children.filter { (_, value) -> value !is KOObjectContainer }
}

fun Map<String, String?>.toObjectContainer() = entries.fold(KOObjectContainer()) { root, (key, value) ->
  val log = KOLogger()

  if (value != null) {
    val parts = key.split(".")
    val name = parts.last()
    var container = root
    val prefixParts = parts.subList(0, parts.size - 1)
    val prefixIterator = prefixParts.listIterator()

    //log("Name=${name},Prefix=${prefixParts.joinToString(", ")}=${value}")
    while (prefixIterator.hasNext()) {
      container = container.getContainer(prefixIterator.next())
    }

    val typedValue = run {
      try {
        when {
          value.matches(Regex("^[0-9.]+$")) && value.toList().count { it == '.' } < 2 ->
            if (value.contains("."))
              value.toDouble()
            else
              value.toInt()

          arrayOf("true", "false").contains(value.toLowerCase()) -> value.toBoolean()

          else -> value
        }
      } catch (cause: Throwable) {
        log("Unable to parse value: ${value}", cause)
        value
      }
    }

    container.set(name, typedValue as Any)
  }
  root
}