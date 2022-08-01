package ts2mls.types

import mlscript._

object Converter {
  private val primitiveName = Map[String, Type](
    "boolean" -> TypeName("bool"),
    "number" -> TypeName("number"),
    "string" -> TypeName("string"),
    "any" -> Top,
    "void" -> TypeName("unit"),
    "null" -> TypeName("null"),
    "undefined" -> TypeName("undefined"),
    "never" -> Bot,
    "object" -> Record(List())
  )

  def convert(tsType: TSType): Type = tsType match {
    case TSNamedType(typeName) => primitiveName(typeName)
    case TSFunctionType(params, res, constraint) =>
      if (params.length == 0) Function(primitiveName("void"), convert(res))
      else params.foldRight[Type](convert(res))((tst, mlst) => Function(convert(tst), mlst))
    case TSUnionType(lhs, rhs) => Union(convert(lhs), convert(rhs))
    case TSIntersectionType(lhs, rhs) => Inter(convert(lhs), convert(rhs))
    case v: TSTypeVariable => convertTypeVariable(v)
    case _ => new TypeName("") // TODO: more types support
  }

  // TODO: only for intersection test
  private def convertTypeVariable(tstv: TSTypeVariable): Type = TypeName(tstv.toString())
}