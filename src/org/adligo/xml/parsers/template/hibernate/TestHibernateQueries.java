package org.adligo.xml.parsers.template.hibernate;

import java.sql.SQLException;
import java.util.List;

import org.adligo.models.params.client.Params;
import org.adligo.models.params.client.SqlOperators;
import org.adligo.tests.ATest;
import org.adligo.tests.xml.parsers.template.jdbc.MockDatabase;
import org.adligo.xml.parsers.template.Template;
import org.adligo.xml.parsers.template.Templates;
import org.adligo.xml.parsers.template.jdbc.BaseSqlOperators;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class TestHibernateQueries extends ATest {
	Templates templates = new Templates();
	
	public void setUp() throws SQLException {
		MockDatabase.createTestDb();
		templates.parseResource("/org/adligo/tests/xml/parsers/template/jdbc/Persons2_0_SQL.xml");
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

	private Session createSession() {
		Configuration config = new Configuration();
		config.configure();
		SessionFactory factory = config.buildSessionFactory();  
		return factory.openSession();
	}
}
