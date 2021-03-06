package org.adligo.xml.parsers.template_hibernate_tests;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;

import org.adligo.models.params.shared.Params;
import org.adligo.models.params.shared.SqlOperators;
import org.adligo.tests.ATest;
import org.adligo.xml.parsers.template.Template;
import org.adligo.xml.parsers.template.Templates;
import org.adligo.xml.parsers.template.jdbc.BaseSqlOperators;
import org.adligo.xml.parsers.template_hibernate.HibernateEngineInput;
import org.adligo.xml.parsers.template_hibernate.HibernateTemplateParserEngine;
import org.adligo.xml.parsers.template_tests.jdbc.MockDatabase;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class TestHibernateQueries extends ATest {
	Templates templates = new Templates();
	
	public void setUp() throws SQLException, IOException {
		MockDatabase.createTestDb();
		templates.parseResource("/org/adligo/xml/parsers/template_tests/jdbc/Persons2_0_SQL.xml");
	}
	
	@SuppressWarnings("unchecked")
	public void testSimpleQueryEntityPopulation() throws Exception {
		Params params = new Params();
		params.addParam("default");
		Params where_params = params.addWhereParams();
		where_params.addParam("fname",SqlOperators.EQUALS, "john");
		
		Template personsTemp = templates.getTemplate("persons");
		
		Session session = createSession();
		
		HibernateEngineInput input = new HibernateEngineInput();
		input.setTemplate(personsTemp);
		input.setSession(session);
		input.setAllowedOperators(BaseSqlOperators.OPERATORS);
		input.setParams(params);
		
		SQLQuery query = HibernateTemplateParserEngine.parse(input);
		query.addEntity(MockPerson.class);
		List<MockPerson> persons = (List<MockPerson>) query.list();
		
		assertEquals(1, persons.size());
		MockPerson person = persons.get(0);
		assertEquals(new Integer(1), person.getTid());
		assertEquals(new Integer(0), person.getVersion());
		assertEquals("john", person.getFname());
		assertEquals("doe", person.getLname());
		
		System.out.println("yea " + person.getFname());
	}

	@SuppressWarnings("unchecked")
	public void test2PartSimpleQueryEntityPopulation() throws Exception {
		Params params = new Params();
		params.addParam("default");
		Params where_params = params.addWhereParams();
		where_params.addParam("fname",SqlOperators.EQUALS, "john");
		where_params.addParam("lname",SqlOperators.EQUALS, "doe");
		
		Template personsTemp = templates.getTemplate("persons");
		
		Session session = createSession();
		
		HibernateEngineInput input = new HibernateEngineInput();
		input.setTemplate(personsTemp);
		input.setSession(session);
		input.setAllowedOperators(BaseSqlOperators.OPERATORS);
		input.setParams(params);
		
		SQLQuery query = HibernateTemplateParserEngine.parse(input);
		query.addEntity(MockPerson.class);
		List<MockPerson> persons = (List<MockPerson>) query.list();
		
		assertEquals(1, persons.size());
		MockPerson person = persons.get(0);
		assertEquals(new Integer(1), person.getTid());
		assertEquals(new Integer(0), person.getVersion());
		assertEquals("john", person.getFname());
		assertEquals("doe", person.getLname());
		
		System.out.println("yea " + person.getFname());
	}
	private Session createSession() throws IOException {
		Configuration config = new Configuration();

		InputStream is = TestHibernateQueries.class.getResourceAsStream(
				"/org/adligo/xml/parsers/template_hibernate_tests/TestPerson.hbm.xml");
		config.addInputStream(is);
		config.configure("/org/adligo/xml/parsers/template_hibernate_tests/hibernate.cfg.xml");
		SessionFactory factory = config.buildSessionFactory();  
		is.close();
		return factory.openSession();
	}
}
