/*******************************************************************************
 *  Copyright (c) 2015 Bosch Software Innovations GmbH and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  and Eclipse Distribution License v1.0 which accompany this distribution.
 *   
 *  The Eclipse Public License is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *  The Eclipse Distribution License is available at
 *  http://www.eclipse.org/org/documents/edl-v10.php.
 *   
 *  Contributors:
 *  Bosch Software Innovations GmbH - Please refer to git log
 *******************************************************************************/
package org.eclipse.vorto.codegen.tests.mapping;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.vorto.codegen.api.ICodeGenerator;
import org.eclipse.vorto.codegen.api.mapping.IMappingRule;
import org.eclipse.vorto.codegen.api.mapping.IMappingRules;
import org.eclipse.vorto.codegen.api.mapping.IMappingRulesAware;
import org.eclipse.vorto.codegen.internal.mapping.DefaultMappingRules;
import org.eclipse.vorto.core.api.model.datatype.DatatypeFactory;
import org.eclipse.vorto.core.api.model.datatype.PrimitivePropertyType;
import org.eclipse.vorto.core.api.model.datatype.PrimitiveType;
import org.eclipse.vorto.core.api.model.datatype.Property;
import org.eclipse.vorto.core.api.model.functionblock.Configuration;
import org.eclipse.vorto.core.api.model.functionblock.FunctionBlock;
import org.eclipse.vorto.core.api.model.functionblock.FunctionblockFactory;
import org.eclipse.vorto.core.api.model.functionblock.FunctionblockModel;
import org.eclipse.vorto.core.api.model.informationmodel.FunctionblockProperty;
import org.eclipse.vorto.core.api.model.informationmodel.InformationModel;
import org.eclipse.vorto.core.api.model.informationmodel.InformationModelFactory;
import org.eclipse.vorto.core.api.model.mapping.Attribute;
import org.eclipse.vorto.core.api.model.mapping.MappingFactory;
import org.eclipse.vorto.core.api.model.mapping.MappingModel;
import org.eclipse.vorto.core.api.model.mapping.Rule;
import org.eclipse.vorto.core.api.model.mapping.StereoType;
import org.eclipse.vorto.core.api.model.mapping.TargetElement;
import org.junit.Test;

public class CodeGeneratorWithMappingsTest {

	@Test
	public void testGetRuleForInfoModel() {
		InformationModel leftSideModel = createInformationModel();

		final List<IMappingRule> rules = new ArrayList<>();

		ICodeGenerator<InformationModel> generator = new AbstractGenerator() {

			@Override
			public void generate(InformationModel ctx, IProgressMonitor monitor) {

				rules.addAll(mappingRules
						.getRulesContainStereoType("configDescription"));
			}
		};

		MappingModel mappingModel = this.createRuleModel(leftSideModel);

		((IMappingRulesAware) generator)
				.setMappingRules(new DefaultMappingRules(mappingModel));

		generator.generate(leftSideModel, null);

		assertEquals(1, rules.size());
		assertEquals("brightness",
				rules.get(0)
						.getStereoTypeAttribute("configDescription", "name")
						.getValue());
		assertEquals("Number",
				rules.get(0)
						.getStereoTypeAttribute("configDescription", "type")
						.getValue());
	}

	private abstract class AbstractGenerator implements
			ICodeGenerator<InformationModel>, IMappingRulesAware {

		protected IMappingRules mappingRules;

		@Override
		public String getMappingRulesFileName() {
			return "demo.mapping";
		}

		@Override
		public void setMappingRules(IMappingRules rules) {
			this.mappingRules = rules;
		}

		@Override
		public String getName() {
			return "DemoPlatform";
		}

	}

	private InformationModel createInformationModel() {
		InformationModel infoModel = InformationModelFactory.eINSTANCE
				.createInformationModel();
		infoModel.setName("BoschXYZ");

		FunctionblockModel fbm = FunctionblockFactory.eINSTANCE
				.createFunctionblockModel();
		fbm.setName("Switcher");

		FunctionBlock fb = FunctionblockFactory.eINSTANCE.createFunctionBlock();
		Configuration config = FunctionblockFactory.eINSTANCE
				.createConfiguration();
		Property property = DatatypeFactory.eINSTANCE.createProperty();

		PrimitivePropertyType p = DatatypeFactory.eINSTANCE
				.createPrimitivePropertyType();
		property.setName("brightness");
		p.setType(PrimitiveType.INT);

		property.setType(p);

		config.getProperties().add(property);

		fbm.setFunctionblock(fb);
		infoModel.getProperties()
				.add(createPropertyFromFunctionBlockModel(fbm));
		return infoModel;
	}

	private static FunctionblockProperty createPropertyFromFunctionBlockModel(
			FunctionblockModel fbm) {
		FunctionblockProperty fbp = InformationModelFactory.eINSTANCE
				.createFunctionblockProperty();
		fbp.setName("fbm1");
		fbp.setType(fbm);
		return fbp;
	}

	/*
	 * mapping { model BoschXYZ target smarthome
	 * 
	 * from Switcher.configuration.brightness to "configDescription" with {name:
	 * "brightness", type: "Number",}
	 * 
	 * }
	 */
	private MappingModel createRuleModel(InformationModel infoModel) {
		MappingModel mappingModel = MappingFactory.eINSTANCE
				.createMappingModel();
		mappingModel.setInfomodel(infoModel);
		mappingModel.getRules().add(createeRule());
		return mappingModel;
	}

	private static Rule createeRule() {
		Rule rule = MappingFactory.eINSTANCE.createRule();
		TargetElement targetElement = MappingFactory.eINSTANCE
				.createTargetElement();
		StereoType stereoType = MappingFactory.eINSTANCE.createStereoType();
		Attribute typeAttribute = MappingFactory.eINSTANCE.createAttribute();
		typeAttribute.setName("type");
		typeAttribute.setValue("Number");

		Attribute nameAttribute = MappingFactory.eINSTANCE.createAttribute();
		nameAttribute.setName("name");
		nameAttribute.setValue("brightness");

		stereoType.setName("configDescription");
		stereoType.getAttributes().add(nameAttribute);
		stereoType.getAttributes().add(typeAttribute);

		targetElement.getStereoTypes().add(stereoType);

		rule.setTargetElement(targetElement);
		return rule;
	}

}
