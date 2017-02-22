import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.vocabulary.RDF;
import org.apache.xerces.*;

import chesspresso.game.Game;
import chesspresso.move.Move;
import chesspresso.pgn.PGNReader;
import chesspresso.pgn.PGNSyntaxError;

public class pgnToRDF {
	public static final String DATA_URI = "http://purl.org/NET/rdfchess/id/";
	public static final String ONTOLOGY_URI = "http://purl.org/NET/rdfchess/";
	public static final String ONTOLOGY_NAME_1 = "chessonto";
	public static final String ONTOLOGY_NAME_2 = "chessonto-view";
	public static final String ONTOLOGY_NAME_3 = "chessonto-full";

	/**
	 * http://purl.org/NET/rdfchess/id/ssss for non-literal resources in the
	 * linked datasets that are not a vocabulary term where ssss is a random,
	 * unambiguous string; http://purl.org/NET/rdfchess/ontology-name for the
	 * name of ontologies; http://purl.org/NET/rdfchess/ontology-name#ClassName
	 * for class names defined in the ontology named ontology-name;
	 * http://purl.org/NET/rdfchess/ontology-name#propertyName for property
	 * names defined in the ontology named ontology-name;
	 * http://purl.org/NET/rdfchess/ontology-name#named_individual for named
	 * individual defined in the ontology named ontology-name.
	 */
	/**
	 * Converts from PGN to RDF
	 */
	public static Model pgn2rdf(Game g) {
		Model modelo = ModelFactory.createDefaultModel();
		Resource r = modelo.createResource(DATA_URI
				+ getUUID("gam", 5));
		Resource r2 = modelo
				.createResource("http://purl.org/NET/rdfchess/ontology/ChessGame");
		Property rdf_type = modelo.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
		modelo.add(r, rdf_type, r2);
		
		Resource r_place = modelo.createResource(ONTOLOGY_URI + ONTOLOGY_NAME_1
				+ ":" + "Place");
		Property p_atPlace = modelo.createProperty(ONTOLOGY_URI
				+ ONTOLOGY_NAME_1 + ":" + "atPlace");
		Property p_atTime = modelo.createProperty(ONTOLOGY_URI
				+ ONTOLOGY_NAME_1 + ":" + "atTime");
		Resource r19 = modelo.createResource(DATA_URI
				+ getUUID("pla", 6));
		modelo.add(r, p_atPlace, r19);
		modelo.add(r19, rdf_type, r_place);
		Property p_hasName = modelo.createProperty(ONTOLOGY_URI
				+ ONTOLOGY_NAME_1 + ":" + "hasName");
		modelo.add(r19, p_hasName, g.getSite()+"^^xsd:string");
		modelo.add(r, p_atTime, g.getDate()+"^^xsd:dateTime");

		Resource r3 = modelo.createResource(ONTOLOGY_URI + ONTOLOGY_NAME_1
				+ ":" + "ChessTournament");
		Resource r4 = modelo.createResource(DATA_URI
				+  getUUID("tou", 5));
		modelo.add(r4, p_hasName, g.getEvent()+"^^xsd:string");
		modelo.add(r4, rdf_type, r3);
		modelo.add(r4, p_atPlace, r19);
		modelo.add(r4, p_atTime, g.getEventDate()+"^^xsd:dateTime");
		Property p_subEventOf = modelo
				.createProperty("http://semanticweb.cs.vu.nl/2009/11/sem/subEventOf");
		modelo.add(r, p_subEventOf, r4);
		

		Resource r5 = modelo.createResource(ONTOLOGY_URI + ONTOLOGY_NAME_1
				+ ":" + "ChessOpening");
		Resource r6 = modelo.createResource(DATA_URI
				+ getUUID("ope", 6));
		modelo.add(r6, rdf_type, r5);
		Property p3 = modelo.createProperty(ONTOLOGY_URI + ONTOLOGY_NAME_1
				+ ":" + "hasOpening");
		modelo.add(r, p3, r6);
		Property p4 = modelo.createProperty(ONTOLOGY_URI + ONTOLOGY_NAME_1
				+ ":" + "hasECOCode");
		Resource r7 = modelo.createResource(g.getECO()+"^^xsd:string");
		modelo.add(r6, p4, r7);

		Property p5 = modelo.createProperty(ONTOLOGY_URI + ONTOLOGY_NAME_1
				+ ":" + "hasResult");
		Resource r8 = modelo.createResource(ONTOLOGY_URI + ONTOLOGY_NAME_1
				+ ":" + "ChessGameResult");
		Resource r9 = modelo.createResource(DATA_URI
				+ getUUID("res", 7));
		modelo.add(r9, rdf_type, r8);
		modelo.add(r, p5, r9);
		Property p6 = modelo.createProperty(ONTOLOGY_URI + ONTOLOGY_NAME_1
				+ ":" + "encodedAsSAN");
		Resource r10 = modelo.createResource(g.getResultStr()+"^^xsd:string"); /*
																 * ask Adila
																 * what is SAN
																 * here?
																 */
		modelo.add(r9, p6, r10);

		Resource r_AgentRole = modelo.createResource(ONTOLOGY_URI
				+ ONTOLOGY_NAME_1 + ":" + "AgentRole");
		Property p_providesAgentRole = modelo
				.createProperty("http://www.w3.org/2000/01/rdf-schema#"
						+ "providesAgentRole");
		Resource r_AgentRoleWhite = modelo.createResource(DATA_URI
				+ getUUID("rol", 6));
		Resource r_AgentRoleBlack = modelo.createResource(DATA_URI
				+ getUUID("rol", 6));
		modelo.add(r, p_providesAgentRole, r_AgentRoleWhite);
		modelo.add(r, p_providesAgentRole, r_AgentRoleBlack);
		Property p_subClassOf = modelo
				.createProperty("http://www.w3.org/2000/01/rdf-schema#"
						+ "subClassOf");
		Resource r_WhitePlayerRole = modelo.createResource(ONTOLOGY_URI + ONTOLOGY_NAME_1
				+ ":" + "WhitePlayerRole");
		modelo.add(r_AgentRoleWhite, rdf_type, r_WhitePlayerRole);
		Resource r_BlackPlayerRole = modelo.createResource(ONTOLOGY_URI + ONTOLOGY_NAME_1
				+ ":" + "BlackPlayerRole");
		modelo.add(r_AgentRoleBlack, rdf_type, r_BlackPlayerRole);
		modelo.add(r_WhitePlayerRole, p_subClassOf, r_AgentRole); // WhitePlayerRole
													// subclassOf AgentRole
		modelo.add(r_BlackPlayerRole, p_subClassOf, r_AgentRole); // BlackPlayerRole
													// subclassOf AgentRole
		Property p_performedBy = modelo.createProperty(ONTOLOGY_URI
				+ ONTOLOGY_NAME_1 + ":" + "performedBy");
		Resource r_WhitePlayer = modelo.createResource(DATA_URI
				+ getUUID("ag", 6));
		modelo.add(r_WhitePlayerRole, p_performedBy, r_WhitePlayer); // WhitePlayerRole
														// performedBy
														// r_WhitePlayer
		Resource r_Agent = modelo.createResource(ONTOLOGY_URI + ONTOLOGY_NAME_1
				+ ":" + "Agent");
		modelo.add(r_WhitePlayer, rdf_type, r_Agent); // WhitePlayer hasType
														// Agent
		RDFNode rdfWhite = null;
		String sWhite = g.getWhite();
		if (sWhite.startsWith("http")) {
			rdfWhite = modelo.createResource(sWhite);
			modelo.add(r_WhitePlayer, p_hasName, rdfWhite+"^^xsd:string"); /*
															 * r14 hasName
															 * whitePlayer'sURI
															 * for dereferencing
															 * later
															 */
		} else {
			rdfWhite = modelo.createLiteral(sWhite);
			modelo.add(r_WhitePlayer, p_hasName, rdfWhite+"^^xsd:string"); /*
															 * r14 hasName
															 * whitePlayer's
															 * name
															 */
		}
		Resource r_BlackPlayer = modelo.createResource(DATA_URI
				+  getUUID("ag", 6));
		modelo.add(r_BlackPlayerRole, p_performedBy, r_BlackPlayer); /* BlackPlayerRole
														 performedBy
														 r_BlackPlayer*/
		modelo.add(r_BlackPlayer, rdf_type, r_Agent); /* r_BlackPlayer hasType Agent*/
		RDFNode rdfBlack = null;
		String sBlack = g.getBlack();
		if (sBlack.startsWith("http")) {
			rdfBlack = modelo.createResource(sBlack);
			modelo.add(r_BlackPlayer, p_hasName, rdfBlack+"^^xsd:string"); /*
															 * r16 hasName
															 * BlackPlayer'sURI
															 * for dereferencing
															 * later
															 */
		} else {
			rdfBlack = modelo.createLiteral(sBlack);
			modelo.add(r_BlackPlayer, p_hasName, rdfBlack+"^^xsd:string"); /*
															 * r16 hasName
															 * whitePlayer's
															 * name
															 */
		}

		Resource r17 = modelo.createResource(ONTOLOGY_URI + ONTOLOGY_NAME_1
				+ ":" + "HalfMove");
		modelo.add(r17, p_subEventOf, r); // halfMove subEventOf ChessGame
		Property p_nextHalfMove = modelo.createProperty(ONTOLOGY_URI
				+ ONTOLOGY_NAME_1 + ":" + "nextHalfMove");
		Property p_hasHalfMove = modelo.createProperty(ONTOLOGY_URI
				+ ONTOLOGY_NAME_1 + ":" + "hasHalfMove");
		Property p_hasFirstHalfMove = modelo.createProperty(ONTOLOGY_URI
				+ ONTOLOGY_NAME_1 + ":" + "hasFirstHalfMove");
		Property p_hasLastHalfMove = modelo.createProperty(ONTOLOGY_URI
				+ ONTOLOGY_NAME_1 + ":" + "hasLastHalfMove");
		Property p_hasSANRecord = modelo.createProperty(ONTOLOGY_URI
				+ ONTOLOGY_NAME_1 + ":" + "hasSANRecord");
		Resource r_ActingPlayerRole = modelo.createResource(ONTOLOGY_URI
				+ ONTOLOGY_NAME_1 + ":" + "ActingPlayerRole");
		modelo.add(r_ActingPlayerRole, p_subClassOf, r_AgentRole);
		
		g.gotoStart();
		Resource previousMove = null;
		while (g.hasNextMove()) {
			Move m = g.getNextMove();
			Resource currentHalfMove = modelo.createResource(DATA_URI
					+ getUUID("hmgam", 7));
			modelo.add(currentHalfMove, rdf_type, r17); /*
														 * currentHalfMove
														 * hasType HalfMove
														 */
			modelo.add(currentHalfMove, p_hasSANRecord, m.getSAN()+"^^xsd:string");
			modelo.add(r, p_hasHalfMove, currentHalfMove);
			// modelo.add(currentHalfMove, p_providesAgentRole, r_AgentRole);
			Resource r_currentActingPlayer = modelo.createResource(DATA_URI
					+ getUUID("rolhg", 8));
			modelo.add(currentHalfMove, p_providesAgentRole,
					r_currentActingPlayer);
			modelo.add(r_currentActingPlayer, rdf_type, r_ActingPlayerRole);
			if (m.isWhiteMove()) {
				modelo.add(r_currentActingPlayer, p_performedBy, r_WhitePlayer);
			} else {
				modelo.add(r_currentActingPlayer, p_performedBy, r_BlackPlayer);
			}
			if (previousMove != null) {
				modelo.add(previousMove, p_nextHalfMove, currentHalfMove);
			} else {
				modelo.add(r, p_hasFirstHalfMove, currentHalfMove);
			}
			previousMove = currentHalfMove;
			g.goForward();
		}
		modelo.add(r, p_hasLastHalfMove, previousMove);

		Resource r20 = modelo.createResource(ONTOLOGY_URI
				+ ONTOLOGY_NAME_1 + ":" + "ChessGameReport");
		Resource r_cgr = modelo.createResource(DATA_URI
				+ getUUID("cgr", 6));
		Property p_hasReport = modelo.createProperty(ONTOLOGY_URI
				+ ONTOLOGY_NAME_1 + ":" + "hasReport");
		modelo.add(r, p_hasReport, r_cgr);
		modelo.add(r_cgr, rdf_type, r20);
		Resource r_AuthorRoleID = modelo.createResource(DATA_URI
				+ getUUID("rol", 6));
		modelo.add(r_cgr, p_providesAgentRole, r_AuthorRoleID);
		Property p_hasPGNFile = modelo.createProperty(ONTOLOGY_URI
				+ ONTOLOGY_NAME_1 + ":" + "hasPGNFile");
		modelo.add(r_cgr, p_hasPGNFile, ""+"^^xsd:string"); //location of PGN files not existing in the current dataset as of 16thMay,2016
		Resource rAuthorRole = modelo.createResource(ONTOLOGY_URI
				+ ONTOLOGY_NAME_1 + ":" + "AuthorRole");
		modelo.add(r_AuthorRoleID, rdf_type, rAuthorRole);
		Resource r_PGNAuthor = modelo.createResource(DATA_URI
				+ getUUID("ag", 6));
		modelo.add(r_AuthorRoleID, p_performedBy, r_PGNAuthor);
		
		return modelo;
	}

	/**
	 * Generates a random UUID with a certain prefix
	 */
	
	public static String getUUID(String prefix, int l){
		char[] chars = "abcdefghijklmnopqrstuvwxyzABSDEFGHIJKLMNOPQRSTUVWXYZ1234567890".toCharArray();
		Random rand = new Random(System.currentTimeMillis());
		char[] id = new char[l];
		for(int i=0;i<id.length;i++) {
			id[i] = chars[rand.nextInt(chars.length)];
		}
		String s = new String(id);
		return prefix.concat(s);
	} 
	
	/**
	 * Obtains the chessid for a given model
	 */
	public static String getChessId(Model model) {
		String id = "";
		Resource r2 = model
				.createResource("http://purl.org/NET/rdfchess/ontology/ChessGame");
		ResIterator rit = model.listSubjectsWithProperty(RDF.type, r2);
		while (rit.hasNext()) {
			Resource r = rit.next();
			id = r.getURI();
		}
		return id;
	}
	/**
	 * Converts from PGN to RDF according to the views
	 */
	public static Model pgn2rdf_view(Game g) {
		Model modelo = ModelFactory.createDefaultModel();
		Resource r = modelo.createResource(DATA_URI
				+ getUUID("gam", 5));
		Resource r2 = modelo
				.createResource("http://purl.org/NET/rdfchess/ontology/ChessGame");
		Property rdf_type = modelo.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
		modelo.add(r, rdf_type, r2);
		
		Property p_atChessTournament = modelo.createProperty(ONTOLOGY_URI
				+ ONTOLOGY_NAME_2 + ":" + "atChessTournament");
		modelo.add(r, p_atChessTournament, g.getEvent()+"^^xsd:string");
		
		Property p_atPlaceNamed = modelo.createProperty(ONTOLOGY_URI
				+ ONTOLOGY_NAME_2 + ":" + "atPlaceNamed");
		modelo.add(r, p_atPlaceNamed, g.getSite()+"^^xsd:string");
		
		Property p_atTime = modelo.createProperty(ONTOLOGY_URI
				+ ONTOLOGY_NAME_2 + ":" + "atTime");
		modelo.add(r, p_atTime, g.getEventDate()+"^^xsd:dateTime");
		
		Property p_hasOpenningECO = modelo.createProperty(ONTOLOGY_URI
				+ ONTOLOGY_NAME_2 + ":" + "hasOpenningECO");
		modelo.add(r, p_hasOpenningECO, g.getECO()+"^^xsd:string");
		
		Property p_hasResultSAN = modelo.createProperty(ONTOLOGY_URI
				+ ONTOLOGY_NAME_2 + ":" + "hasResultSAN");
		modelo.add(r, p_hasResultSAN, g.getResultStr()+"^^xsd:string");
		
		Property p_hasWhitePlayer = modelo.createProperty(ONTOLOGY_URI
				+ ONTOLOGY_NAME_2 + ":" + "hasWhitePlayer");
		modelo.add(r, p_hasWhitePlayer, g.getWhite()+"^^xsd:string");
		
		Property p_hasBlackPlayer = modelo.createProperty(ONTOLOGY_URI
				+ ONTOLOGY_NAME_2 + ":" + "hasBlackPlayer");
		modelo.add(r, p_hasBlackPlayer, g.getBlack()+"^^xsd:string");
		
		
		Resource r17 = modelo.createResource(ONTOLOGY_URI + ONTOLOGY_NAME_2
				+ ":" + "HalfMove");
		Property p_subEventOf = modelo
				.createProperty("http://semanticweb.cs.vu.nl/2009/11/sem/subEventOf");
		modelo.add(r17, p_subEventOf, r); // halfMove subEventOf ChessGame
		Property p_nextHalfMove = modelo.createProperty(ONTOLOGY_URI
				+ ONTOLOGY_NAME_2 + ":" + "nextHalfMove");
		Property p_hasHalfMove = modelo.createProperty(ONTOLOGY_URI
				+ ONTOLOGY_NAME_2 + ":" + "hasHalfMove");
		Property p_hasFirstHalfMove = modelo.createProperty(ONTOLOGY_URI
				+ ONTOLOGY_NAME_2 + ":" + "hasFirstHalfMove");
		Property p_hasLastHalfMove = modelo.createProperty(ONTOLOGY_URI
				+ ONTOLOGY_NAME_2 + ":" + "hasLastHalfMove");
		Property p_hasSANRecord = modelo.createProperty(ONTOLOGY_URI
				+ ONTOLOGY_NAME_2 + ":" + "hasSANRecord");
		
		g.gotoStart();
		Resource previousMove = null;
		while (g.hasNextMove()) {
			Move m = g.getNextMove();
			Resource currentHalfMove = modelo.createResource(DATA_URI
					+ getUUID("hmgam", 7));
			modelo.add(currentHalfMove, rdf_type, r17); /*
														 * currentHalfMove
														 * hasType HalfMove
														 */
			modelo.add(currentHalfMove, p_hasSANRecord, m.getSAN()+"^^xsd:string");
			modelo.add(r, p_hasHalfMove, currentHalfMove);
			Property p_playedBy = modelo.createProperty(ONTOLOGY_URI
					+ ONTOLOGY_NAME_2 + ":" + "playedBy");
			if (m.isWhiteMove()) {
				modelo.add(currentHalfMove, p_playedBy, g.getWhite()+"^^xsd:string");
			} else {
				modelo.add(currentHalfMove, p_playedBy, g.getBlack()+"^^xsd:string");
			}
			if (previousMove != null) {
				modelo.add(previousMove, p_nextHalfMove, currentHalfMove);
			} else {
				modelo.add(r, p_hasFirstHalfMove, currentHalfMove);
			}
			previousMove = currentHalfMove;
			g.goForward();
		}
		modelo.add(r, p_hasLastHalfMove, previousMove);

		Resource r20 = modelo.createResource(ONTOLOGY_URI
				+ ONTOLOGY_NAME_2 + ":" + "ChessGameReport");
		Resource r_cgr = modelo.createResource(DATA_URI
				+ getUUID("cgr", 6));
		Property p_hasReport = modelo.createProperty(ONTOLOGY_URI
				+ ONTOLOGY_NAME_2 + ":" + "hasReport");
		modelo.add(r, p_hasReport, r_cgr);
		modelo.add(r_cgr, rdf_type, r20);
		
		Property p_hasAuthor = modelo.createProperty(ONTOLOGY_URI
				+ ONTOLOGY_NAME_2 + ":" + "hasAuthor");
		modelo.add(r_cgr, p_hasAuthor, ""+"^^xsd:string");
		
		Property p_hasPGNFIle = modelo.createProperty(ONTOLOGY_URI
				+ ONTOLOGY_NAME_2 + ":" + "hasPGNFIle");
		modelo.add(r_cgr, p_hasAuthor, ""+"^^xsd:anyURI");
		
		return modelo;
	}
	public static void main(String[] args) {
		try {
			String pgn_file = FileUtils
					.readFileToString(new File("Karpov.pgn"));
			Reader reader = new StringReader(pgn_file);
			PGNReader pgnreader = new PGNReader(reader, "web");
			Game g = null;
			int game_counter = 0;

			try {
				while ((g = pgnreader.parseGame()) != null) {
					StringWriter sw = new StringWriter();
					Model m = pgn2rdf(g);
					game_counter++;
					//String ID = getChessId(m);
					Model m_with_prefix = RDFPrefixes.addPrefixesIfNeeded(m);
					RDFDataMgr.write(sw, m, Lang.NTRIPLES);

					String fileName = "dataset_" +ONTOLOGY_NAME_1+ "Karpov"+ game_counter + ".nt";
					FileWriter out = new FileWriter(fileName);
					try {
						//m.write(out, "N-TRIPLES");
						RDFDataMgr.write(out, m_with_prefix, RDFFormat.TURTLE_PRETTY);
					} finally {
						try {
							out.close();
						} catch (IOException closeException) {
						}
					}

					 //System.out.println(m);
				}
			} catch (PGNSyntaxError e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
