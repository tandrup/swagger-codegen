/**
 *  Copyright 2014 Wordnik, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.wordnik.swagger.codegen

import java.io.File

import com.wordnik.swagger.codegen.model._

import scala.collection.immutable.HashMap

object BasicTypeScriptGenerator extends BasicTypeScriptGenerator {
  def main(args: Array[String]) = generateClient(args)
}

class BasicTypeScriptGenerator extends BasicGenerator {
  override def defaultIncludes = Set(
    "double",
    "int",
    "long",
    "short",
    "char",
    "float",
    "String",
    "boolean",
    "Boolean",
    "Double",
    "Integer",
    "Long",
    "Float")

  /**
   * We are using java objects instead of primitives to avoid showing default
   * primitive values when the API returns missing data.  For instance, having a
   * {"count":0} != count is unknown.  You can change this to use primitives if you
   * desire, but update the default values as well or they'll be set to null in 
   * variable declarations.
   */
  override def typeMapping = Map(
    "Array" -> "Array",
    "array" -> "Array",
    "List" -> "Array",
    "boolean" -> "boolean",
    "string" -> "string",
    "int" -> "number",
    "float" -> "number",
    "number" -> "number",
    "long" -> "number",
    "short" -> "number",
    "char" -> "string",
    "double" -> "number",
    "object" -> "any",
    "integer" -> "number")

  // location of templates
  override def templateDir = "TypeScript"

  // where to write generated code
  override def destinationDir = "generated-code/typescript/"

  // template used for models
  modelTemplateFiles += "model.mustache" -> ".ts"

  // template used for models
  apiTemplateFiles += "api.mustache" -> ".ts"

  override def reservedWords = Set("abstract", "continue", "for", "new", "switch", "assert", 
    "default", "if", "package", "synchronized", "boolean", "do", "goto", "private", 
    "this", "break", "double", "implements", "protected", "throw", "byte", "else", 
    "import", "public", "throws", "case", "enum", "instanceof", "return", "transient", 
    "catch", "extends", "int", "short", "try", "char", "final", "interface", "static", 
    "void", "class", "finally", "const",
    "super", "while")

  // package for models
  override def modelPackage: Option[String] = Some("dco_api_model")

  // package for api classes
  override def apiPackage: Option[String] = Some("dco_api")

  // file suffix
  override def fileSuffix = ".ts"

  override def toVarName(name: String): String = {
    val paramName = name.replaceAll("[^a-zA-Z0-9_]","")
    super.toVarName(paramName)
  }

  override def toApiFilename(name: String): String = {
    val paramName = name.replaceAll("[^a-zA-Z0-9_]","")
    super.toApiFilename(paramName)
  }

  override def toApiName(name: String): String = {
    val paramName = name.replaceAll("[^a-zA-Z0-9_]","")
    super.toApiName(paramName)
  }

  override def processResponseClass(responseClass: String): Option[String] = {
    responseClass match {
      case "void" => None
      case e: String => Some(typeMapping.getOrElse(e, e.capitalize.replaceAll("\\[", "<").replaceAll("\\]", ">")))
    }
  }

  override def processResponseDeclaration(responseClass: String): Option[String] = {
    responseClass match {
      case "void" => None
      case e: String => {
        val ComplexTypeMatcher = "(.*)\\[(.*)\\].*".r
        val t = e match {
          case ComplexTypeMatcher(container, inner) => {
            e.replaceAll(container, typeMapping.getOrElse(container, container))
          }
          case _ => e
        }
        Some(typeMapping.getOrElse(t, t.capitalize.replaceAll("\\[", "<").replaceAll("\\]", ">")))
      }
    }
  }

  var enums = List[Map[String,AnyRef]]()

  override def toDeclaredType(dt: String): String = {
    val declaredType = dt.indexOf("[") match {
      case -1 => dt
      case n: Int => {
        if (dt.substring(0, n) == "Array")
          "Array" + dt.substring(n).replaceAll("\\[", "<").replaceAll("\\]", ">")
        else if (dt.substring(0, n) == "Set")
          "Set" + dt.substring(n).replaceAll("\\[", "<").replaceAll("\\]", ">")
        else dt.replaceAll("\\[", "<").replaceAll("\\]", ">")
      }
    }
    typeMapping.getOrElse(declaredType, declaredType.capitalize)
  }

  override def toDeclaration(obj: ModelProperty) = {
    var declaredType = toDeclaredType(obj.`type`)

    if (!obj.`type`.equals("string")) {
      obj.allowableValues match {
        case AllowableListValues(enumList,_) => {
          val outputDirectory = (destinationDir + File.separator + modelPackage.getOrElse("").replace(".", File.separator))
          val m = HashMap[String, AnyRef](
            "models" -> List(Map(
              "model" -> HashMap[String, AnyRef](
                "enum" -> Boolean.box(true),
                "classname" -> obj.`type`,
                "values" -> enumList,
                "classVarName" -> ""
              )
            )),
            "package" -> modelPackage,
            "outputDirectory" -> outputDirectory,
            "filename" -> toModelFilename(obj.`type`)
          );
          enums = m :: enums;
        }
        case _ => {
          // do nothing
        }
      }
    }

    val defaultValue = toDefaultValue(declaredType, obj)
    declaredType match {
      case "Array" => {
        val inner = {
          obj.items match {
            case Some(items) => items.ref.getOrElse(items.`type`)
            case _ => {
              println("failed on " + declaredType + ", " + obj)
              throw new Exception("no inner type defined")
            }
          }
        }
        declaredType = toDeclaredType(inner) + "[]"
      }
      case _ =>
    }
    (declaredType, defaultValue)
  }

  override def prepareModelMap(models: Map[String, Model]): List[Map[String, AnyRef]] = {
    val modelList = super.prepareModelMap(models);
    val fullList = enums ::: modelList;
    return fullList;
  }

  override def escapeReservedWord(word: String) = {
    if (reservedWords.contains(word)) 
      throw new Exception("reserved word " + "\"" + word + "\" not allowed")
    else word
  }
}
