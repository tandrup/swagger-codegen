package com.wordnik.swagger.codegen

import com.wordnik.swagger.codegen.model._

object BasicTypeScriptAngularGenerator extends BasicTypeScriptAngularGenerator {
  def main(args: Array[String]) = generateClient(args)
}

class BasicTypeScriptAngularGenerator extends BasicTypeScriptGenerator {
  override def templateDir = "typescript-angular"

  override def destinationDir = "generated-code/typescript-angular/"
}
