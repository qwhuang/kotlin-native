/*
 * Copyright 2010-2018 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the LICENSE file.
 */

package kotlin.native.internal

import kotlin.reflect.KClass

@ExportForCompiler
internal class KClassImpl<T : Any>(private val typeInfo: NativePtr) : KClass<T> {
    override val simpleName: String?
        get() {
            val relativeName = getRelativeName(typeInfo)
                    ?: return null

            return relativeName.substringAfterLast(".")
        }

    override val qualifiedName: String?
        get() {
            val packageName = getPackageName(typeInfo)
                    ?: return null

            val relativeName = getRelativeName(typeInfo)!!
            return if (packageName.isEmpty()) {
                relativeName
            } else {
                "$packageName.$relativeName"
            }
        }

    override fun isInstance(value: Any?): Boolean = value != null && isInstance(value, this.typeInfo)

    override fun equals(other: Any?): Boolean =
            other is KClassImpl<*> && this.typeInfo == other.typeInfo

    override fun hashCode(): Int = typeInfo.hashCode()

    override fun toString(): String {
        return "class " + (qualifiedName ?: simpleName ?: "<anonymous>")
    }
}

@ExportForCompiler
@SymbolName("Kotlin_Any_getTypeInfo")
internal external fun getObjectTypeInfo(obj: Any): NativePtr

@ExportForCompiler
@Intrinsic
internal external inline fun <reified T : Any> getClassTypeInfo(): NativePtr

@SymbolName("Kotlin_TypeInfo_getPackageName")
private external fun getPackageName(typeInfo: NativePtr): String?

@SymbolName("Kotlin_TypeInfo_getRelativeName")
private external fun getRelativeName(typeInfo: NativePtr): String?

@SymbolName("Kotlin_TypeInfo_isInstance")
private external fun isInstance(obj: Any, typeInfo: NativePtr): Boolean
