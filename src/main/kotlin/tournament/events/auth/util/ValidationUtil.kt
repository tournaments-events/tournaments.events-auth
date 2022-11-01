package tournament.events.auth.util

import javax.validation.ConstraintViolation
import javax.validation.ElementKind

val <T> ConstraintViolation<T>.pathAsString: String
    get() {
        val propertyPath = StringBuilder()
        val i = this.propertyPath.iterator()
        while (i.hasNext()) {
            val node = i.next()
            if (node.kind == ElementKind.METHOD || node.kind == ElementKind.CONSTRUCTOR) {
                continue
            }
            propertyPath.append(node.name)
            if (node.index != null) {
                propertyPath.append(String.format("[%d]", node.index))
            }
            if (i.hasNext()) {
                propertyPath.append('.')
            }
        }
        return propertyPath.toString()
    }
