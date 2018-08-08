package com.rcggs.vdm.service.codegen;

import java.io.File;
import java.nio.file.Paths;

import org.jsonschema2pojo.SchemaMapper;

import com.sun.codemodel.JCodeModel;

public class JsonSchemaGenerator {

	public static void main(String[] args) throws Exception {
		generate("com.rcggs.vdm.service.model", "ResourceRequest");
	}

	public static void generate(final String packageName, final String className) {
		JCodeModel codeModel = new JCodeModel();
		try {
			new SchemaMapper().generate(codeModel, className, packageName,
					Paths.get("/vbox/enable-workspace/vdm-service/schema/RawFile.schema").toUri().toURL());
			codeModel.build(new File("/tmp/"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
