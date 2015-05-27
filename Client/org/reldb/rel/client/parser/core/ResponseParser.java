/* Generated By:JavaCC: Do not edit this line. ResponseParser.java */
package org.reldb.rel.client.parser.core;
import org.reldb.rel.client.parser.ResponseHandler;
import org.reldb.rel.client.parser.ResponseAdapter;
import org.reldb.rel.utilities.StringUtils;
@SuppressWarnings("all")
public class ResponseParser implements ResponseParserConstants {
        private ResponseHandler responseHandler = new ResponseAdapter();
        public void setResponseHandler(ResponseHandler handler) {
                responseHandler = handler;
        }

/***********************************************
 *          THE GRAMMAR STARTS HERE            *
 ***********************************************/
  final public void parse() throws ParseException {
    literal(0);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case EOT:
      jj_consume_token(EOT);
      break;
    case 0:
      jj_consume_token(0);
      break;
    default:
      jj_la1[0] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  final public void literal(int depth) throws ParseException {
    if (jj_2_1(3)) {
      tuple_relation_or_array(depth);
    } else {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case ARRAY:
      case FALSE:
      case RELATION:
      case TRUE:
      case TUPLE:
      case INTEGER_LITERAL:
      case FLOATING_POINT_LITERAL:
      case STRING_LITERAL:
      case IDENTIFIER:
        scalar(depth);
        break;
      default:
        jj_la1[1] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
  }

  final public void tuple_relation_or_array(int depth) throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case TUPLE:
      tuple(depth);
      break;
    case ARRAY:
    case RELATION:
      relation_or_array(depth);
      break;
    default:
      jj_la1[2] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  final public void tuple(int depth) throws ParseException {
    jj_consume_token(TUPLE);
         responseHandler.beginTuple(depth);
    jj_consume_token(LBRACE);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case IDENTIFIER:
      tuple_component(depth);
      label_1:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case COMMA:
          ;
          break;
        default:
          jj_la1[3] = jj_gen;
          break label_1;
        }
        jj_consume_token(COMMA);
        tuple_component(depth);
      }
      break;
    default:
      jj_la1[4] = jj_gen;
      ;
    }
    jj_consume_token(RBRACE);
         responseHandler.endTuple(depth);
  }

  final public void tuple_component(int depth) throws ParseException {
 String s;
    s = identifier();
                          responseHandler.attributeNameInTuple(depth, s);
    literal(1);
  }

  final public void relation_or_array(int depth) throws ParseException {
 String htype;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case RELATION:
      jj_consume_token(RELATION);
                      htype="RELATION";
      break;
    case ARRAY:
      jj_consume_token(ARRAY);
                                                    htype="ARRAY";
      break;
    default:
      jj_la1[5] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
         responseHandler.beginContainer(depth, htype);
    if (jj_2_2(2147483647)) {
      heading(htype);
    } else {
      ;
    }
    jj_consume_token(LBRACE);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case TUPLE:
      tuple(2);
      label_2:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case COMMA:
          ;
          break;
        default:
          jj_la1[6] = jj_gen;
          break label_2;
        }
        jj_consume_token(COMMA);
        tuple(2);
      }
      break;
    default:
      jj_la1[7] = jj_gen;
      ;
    }
    jj_consume_token(RBRACE);
         responseHandler.endContainer(depth);
  }

  final public void heading(String htype) throws ParseException {
    jj_consume_token(LBRACE);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case IDENTIFIER:
                   if (responseHandler.isEmitHeading()) responseHandler.beginHeading(htype);
      attribute_spec();
      label_3:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case COMMA:
          ;
          break;
        default:
          jj_la1[8] = jj_gen;
          break label_3;
        }
        jj_consume_token(COMMA);
        attribute_spec();
      }
                   if (responseHandler.isEmitHeading()) responseHandler.endHeading();
      break;
    default:
      jj_la1[9] = jj_gen;
      ;
    }
    jj_consume_token(RBRACE);
  }

  final public void attribute_spec() throws ParseException {
 String s;
         if (responseHandler.isEmitHeading()) responseHandler.beginAttributeSpec();
    s = identifier();
                          if (responseHandler.isEmitHeading()) responseHandler.attributeName(s);
    type_ref();
         if (responseHandler.isEmitHeading()) responseHandler.endAttributeSpec();
  }

  final public void type_ref() throws ParseException {
 String v; String htype;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case TUPLE:
      jj_consume_token(TUPLE);
                         if (responseHandler.isEmitHeading()) responseHandler.beginTupleDefinition();
      heading("TUPLE");
                                                                                                                         if (responseHandler.isEmitHeading()) responseHandler.endTupleDefinition();
      break;
    case ARRAY:
    case RELATION:
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case RELATION:
        jj_consume_token(RELATION);
                              htype="RELATION";
        break;
      case ARRAY:
        jj_consume_token(ARRAY);
                                                            htype="ARRAY";
        break;
      default:
        jj_la1[10] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
                                                                              if (responseHandler.isEmitHeading()) responseHandler.beginContainerDefinition();
      heading(htype);
                                                                                                                                                                                if (responseHandler.isEmitHeading()) responseHandler.endContainerDefinition();
      break;
    case IDENTIFIER:
      v = identifier();
                                         if (responseHandler.isEmitHeading() && responseHandler.isEmitHeadingTypes()) responseHandler.typeReference(v);
      break;
    case OPERATOR:
      op_type();
      break;
    default:
      jj_la1[11] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  final public String identifier() throws ParseException {
 Token t;
    t = jj_consume_token(IDENTIFIER);
                          {if (true) return t.image;}
    throw new Error("Missing return statement in function");
  }

  final public void op_type() throws ParseException {
         responseHandler.beginOperatorDefinition();
    jj_consume_token(OPERATOR);
    jj_consume_token(LPAREN);
         responseHandler.beginOperatorDefinitionParameters();
    type_ref_commalist();
         responseHandler.endOperatorDefinitionParameters();
    jj_consume_token(RPAREN);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case RETURNS:
                                                                    responseHandler.beginOperatorReturnType();
      jj_consume_token(RETURNS);
      type_ref();
                                                                                                                                      responseHandler.endOperatorReturnType();
      break;
    default:
      jj_la1[12] = jj_gen;
      ;
    }
         responseHandler.endOperatorDefinition();
  }

  final public void type_ref_commalist() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case ARRAY:
    case RELATION:
    case TUPLE:
    case OPERATOR:
    case IDENTIFIER:
      parameter_type();
      label_4:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case COMMA:
          ;
          break;
        default:
          jj_la1[13] = jj_gen;
          break label_4;
        }
        jj_consume_token(COMMA);
                                  responseHandler.emitOperatorParameterSeparator();
        parameter_type();
      }
      break;
    default:
      jj_la1[14] = jj_gen;
      ;
    }
  }

  final public void parameter_type() throws ParseException {
   responseHandler.beginOperatorParameter();
    type_ref();
   responseHandler.endOperatorParameter();
  }

  final public void scalar(int depth) throws ParseException {
         responseHandler.beginScalar(depth);
    possrep(depth, false);
         responseHandler.endScalar(depth);
  }

  final public void possrep(int depth, boolean inPossrep) throws ParseException {
 Token t;
    if (jj_2_3(2)) {
      t = jj_consume_token(IDENTIFIER);
                 responseHandler.beginPossrep(t.image);
      jj_consume_token(LPAREN);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case ARRAY:
      case FALSE:
      case RELATION:
      case TRUE:
      case TUPLE:
      case INTEGER_LITERAL:
      case FLOATING_POINT_LITERAL:
      case STRING_LITERAL:
      case IDENTIFIER:
        possrep(depth, true);
        label_5:
        while (true) {
          switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
          case COMMA:
            ;
            break;
          default:
            jj_la1[15] = jj_gen;
            break label_5;
          }
          jj_consume_token(COMMA);
                                                  responseHandler.separatePossrepComponent();
          possrep(depth, true);
        }
        break;
      default:
        jj_la1[16] = jj_gen;
        ;
      }
      jj_consume_token(RPAREN);
                 responseHandler.endPossrep();
    } else {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case ARRAY:
      case FALSE:
      case RELATION:
      case TRUE:
      case TUPLE:
      case INTEGER_LITERAL:
      case FLOATING_POINT_LITERAL:
      case STRING_LITERAL:
        primitive(inPossrep);
        break;
      default:
        jj_la1[17] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
  }

  final public void primitive(boolean inPossrep) throws ParseException {
 Token t;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case STRING_LITERAL:
      t = jj_consume_token(STRING_LITERAL);
                                responseHandler.primitive((inPossrep) ? t.image : StringUtils.unquote(t.image.substring(1, t.image.length()-1)));
      break;
    case INTEGER_LITERAL:
      t = jj_consume_token(INTEGER_LITERAL);
                                 responseHandler.primitive(t.image);
      break;
    case FLOATING_POINT_LITERAL:
      t = jj_consume_token(FLOATING_POINT_LITERAL);
                                        responseHandler.primitive(t.image);
      break;
    case TRUE:
      t = jj_consume_token(TRUE);
                      responseHandler.primitive(t.image);
      break;
    case FALSE:
      t = jj_consume_token(FALSE);
                       responseHandler.primitive(t.image);
      break;
    case ARRAY:
    case RELATION:
    case TUPLE:
      tuple_relation_or_array(0);
      break;
    default:
      jj_la1[18] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  private boolean jj_2_1(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_1(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(0, xla); }
  }

  private boolean jj_2_2(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_2(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(1, xla); }
  }

  private boolean jj_2_3(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_3(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(2, xla); }
  }

  private boolean jj_3R_29() {
    if (jj_scan_token(OPERATOR)) return true;
    if (jj_scan_token(LPAREN)) return true;
    if (jj_3R_30()) return true;
    if (jj_scan_token(RPAREN)) return true;
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_31()) jj_scanpos = xsp;
    return false;
  }

  private boolean jj_3R_15() {
    if (jj_3R_22()) return true;
    return false;
  }

  private boolean jj_3R_22() {
    if (jj_3R_20()) return true;
    return false;
  }

  private boolean jj_3R_14() {
    if (jj_scan_token(COMMA)) return true;
    if (jj_3R_13()) return true;
    return false;
  }

  private boolean jj_3R_27() {
    if (jj_scan_token(RELATION)) return true;
    return false;
  }

  private boolean jj_3R_26() {
    if (jj_3R_29()) return true;
    return false;
  }

  private boolean jj_3R_25() {
    if (jj_3R_20()) return true;
    return false;
  }

  private boolean jj_3R_24() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_27()) {
    jj_scanpos = xsp;
    if (jj_3R_28()) return true;
    }
    if (jj_3R_7()) return true;
    return false;
  }

  private boolean jj_3R_20() {
    if (jj_scan_token(IDENTIFIER)) return true;
    return false;
  }

  private boolean jj_3R_23() {
    if (jj_scan_token(TUPLE)) return true;
    if (jj_3R_7()) return true;
    return false;
  }

  private boolean jj_3R_21() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_23()) {
    jj_scanpos = xsp;
    if (jj_3R_24()) {
    jj_scanpos = xsp;
    if (jj_3R_25()) {
    jj_scanpos = xsp;
    if (jj_3R_26()) return true;
    }
    }
    }
    return false;
  }

  private boolean jj_3R_17() {
    if (jj_scan_token(ARRAY)) return true;
    return false;
  }

  private boolean jj_3R_9() {
    if (jj_3R_12()) return true;
    return false;
  }

  private boolean jj_3_3() {
    if (jj_scan_token(IDENTIFIER)) return true;
    if (jj_scan_token(LPAREN)) return true;
    return false;
  }

  private boolean jj_3R_8() {
    if (jj_3R_11()) return true;
    return false;
  }

  private boolean jj_3R_6() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_8()) {
    jj_scanpos = xsp;
    if (jj_3R_9()) return true;
    }
    return false;
  }

  private boolean jj_3R_11() {
    if (jj_scan_token(TUPLE)) return true;
    if (jj_scan_token(LBRACE)) return true;
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_15()) jj_scanpos = xsp;
    if (jj_scan_token(RBRACE)) return true;
    return false;
  }

  private boolean jj_3R_34() {
    if (jj_scan_token(COMMA)) return true;
    if (jj_3R_33()) return true;
    return false;
  }

  private boolean jj_3R_28() {
    if (jj_scan_token(ARRAY)) return true;
    return false;
  }

  private boolean jj_3R_10() {
    if (jj_3R_13()) return true;
    Token xsp;
    while (true) {
      xsp = jj_scanpos;
      if (jj_3R_14()) { jj_scanpos = xsp; break; }
    }
    return false;
  }

  private boolean jj_3R_13() {
    if (jj_3R_20()) return true;
    if (jj_3R_21()) return true;
    return false;
  }

  private boolean jj_3_2() {
    if (jj_3R_7()) return true;
    if (jj_scan_token(LBRACE)) return true;
    return false;
  }

  private boolean jj_3R_31() {
    if (jj_scan_token(RETURNS)) return true;
    if (jj_3R_21()) return true;
    return false;
  }

  private boolean jj_3_1() {
    if (jj_3R_6()) return true;
    return false;
  }

  private boolean jj_3R_7() {
    if (jj_scan_token(LBRACE)) return true;
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_10()) jj_scanpos = xsp;
    if (jj_scan_token(RBRACE)) return true;
    return false;
  }

  private boolean jj_3R_32() {
    if (jj_3R_33()) return true;
    Token xsp;
    while (true) {
      xsp = jj_scanpos;
      if (jj_3R_34()) { jj_scanpos = xsp; break; }
    }
    return false;
  }

  private boolean jj_3R_33() {
    if (jj_3R_21()) return true;
    return false;
  }

  private boolean jj_3R_30() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_32()) jj_scanpos = xsp;
    return false;
  }

  private boolean jj_3R_19() {
    if (jj_3R_11()) return true;
    return false;
  }

  private boolean jj_3R_18() {
    if (jj_3R_7()) return true;
    return false;
  }

  private boolean jj_3R_16() {
    if (jj_scan_token(RELATION)) return true;
    return false;
  }

  private boolean jj_3R_12() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_16()) {
    jj_scanpos = xsp;
    if (jj_3R_17()) return true;
    }
    xsp = jj_scanpos;
    if (jj_3R_18()) jj_scanpos = xsp;
    if (jj_scan_token(LBRACE)) return true;
    xsp = jj_scanpos;
    if (jj_3R_19()) jj_scanpos = xsp;
    if (jj_scan_token(RBRACE)) return true;
    return false;
  }

  /** Generated Token Manager. */
  public ResponseParserTokenManager token_source;
  JavaCharStream jj_input_stream;
  /** Current token. */
  public Token token;
  /** Next token. */
  public Token jj_nt;
  private int jj_ntk;
  private Token jj_scanpos, jj_lastpos;
  private int jj_la;
  private int jj_gen;
  final private int[] jj_la1 = new int[19];
  static private int[] jj_la1_0;
  static private int[] jj_la1_1;
  static {
      jj_la1_init_0();
      jj_la1_init_1();
   }
   private static void jj_la1_init_0() {
      jj_la1_0 = new int[] {0x401,0xd47a00,0x5200,0x80000000,0x800000,0x1200,0x80000000,0x4000,0x80000000,0x800000,0x1200,0x80d200,0x20000,0x80000000,0x80d200,0x80000000,0xd47a00,0x547a00,0x547a00,};
   }
   private static void jj_la1_init_1() {
      jj_la1_1 = new int[] {0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,};
   }
  final private JJCalls[] jj_2_rtns = new JJCalls[3];
  private boolean jj_rescan = false;
  private int jj_gc = 0;

  /** Constructor with InputStream. */
  public ResponseParser(java.io.InputStream stream) {
     this(stream, null);
  }
  /** Constructor with InputStream and supplied encoding */
  public ResponseParser(java.io.InputStream stream, String encoding) {
    try { jj_input_stream = new JavaCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source = new ResponseParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 19; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream) {
     ReInit(stream, null);
  }
  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream, String encoding) {
    try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 19; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Constructor. */
  public ResponseParser(java.io.Reader stream) {
    jj_input_stream = new JavaCharStream(stream, 1, 1);
    token_source = new ResponseParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 19; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 19; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Constructor with generated Token Manager. */
  public ResponseParser(ResponseParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 19; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(ResponseParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 19; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      if (++jj_gc > 100) {
        jj_gc = 0;
        for (int i = 0; i < jj_2_rtns.length; i++) {
          JJCalls c = jj_2_rtns[i];
          while (c != null) {
            if (c.gen < jj_gen) c.first = null;
            c = c.next;
          }
        }
      }
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }

  static private final class LookaheadSuccess extends java.lang.Error { }
  final private LookaheadSuccess jj_ls = new LookaheadSuccess();
  private boolean jj_scan_token(int kind) {
    if (jj_scanpos == jj_lastpos) {
      jj_la--;
      if (jj_scanpos.next == null) {
        jj_lastpos = jj_scanpos = jj_scanpos.next = token_source.getNextToken();
      } else {
        jj_lastpos = jj_scanpos = jj_scanpos.next;
      }
    } else {
      jj_scanpos = jj_scanpos.next;
    }
    if (jj_rescan) {
      int i = 0; Token tok = token;
      while (tok != null && tok != jj_scanpos) { i++; tok = tok.next; }
      if (tok != null) jj_add_error_token(kind, i);
    }
    if (jj_scanpos.kind != kind) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) throw jj_ls;
    return false;
  }


/** Get the next Token. */
  final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
    return token;
  }

/** Get the specific Token. */
  final public Token getToken(int index) {
    Token t = token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  private int jj_ntk() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  private java.util.List<int[]> jj_expentries = new java.util.ArrayList<int[]>();
  private int[] jj_expentry;
  private int jj_kind = -1;
  private int[] jj_lasttokens = new int[100];
  private int jj_endpos;

  private void jj_add_error_token(int kind, int pos) {
    if (pos >= 100) return;
    if (pos == jj_endpos + 1) {
      jj_lasttokens[jj_endpos++] = kind;
    } else if (jj_endpos != 0) {
      jj_expentry = new int[jj_endpos];
      for (int i = 0; i < jj_endpos; i++) {
        jj_expentry[i] = jj_lasttokens[i];
      }
      jj_entries_loop: for (java.util.Iterator<?> it = jj_expentries.iterator(); it.hasNext();) {
        int[] oldentry = (int[])(it.next());
        if (oldentry.length == jj_expentry.length) {
          for (int i = 0; i < jj_expentry.length; i++) {
            if (oldentry[i] != jj_expentry[i]) {
              continue jj_entries_loop;
            }
          }
          jj_expentries.add(jj_expentry);
          break jj_entries_loop;
        }
      }
      if (pos != 0) jj_lasttokens[(jj_endpos = pos) - 1] = kind;
    }
  }

  /** Generate ParseException. */
  public ParseException generateParseException() {
    jj_expentries.clear();
    boolean[] la1tokens = new boolean[33];
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 19; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
          if ((jj_la1_1[i] & (1<<j)) != 0) {
            la1tokens[32+j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 33; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.add(jj_expentry);
      }
    }
    jj_endpos = 0;
    jj_rescan_token();
    jj_add_error_token(0, 0);
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = jj_expentries.get(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  /** Enable tracing. */
  final public void enable_tracing() {
  }

  /** Disable tracing. */
  final public void disable_tracing() {
  }

  private void jj_rescan_token() {
    jj_rescan = true;
    for (int i = 0; i < 3; i++) {
    try {
      JJCalls p = jj_2_rtns[i];
      do {
        if (p.gen > jj_gen) {
          jj_la = p.arg; jj_lastpos = jj_scanpos = p.first;
          switch (i) {
            case 0: jj_3_1(); break;
            case 1: jj_3_2(); break;
            case 2: jj_3_3(); break;
          }
        }
        p = p.next;
      } while (p != null);
      } catch(LookaheadSuccess ls) { }
    }
    jj_rescan = false;
  }

  private void jj_save(int index, int xla) {
    JJCalls p = jj_2_rtns[index];
    while (p.gen > jj_gen) {
      if (p.next == null) { p = p.next = new JJCalls(); break; }
      p = p.next;
    }
    p.gen = jj_gen + xla - jj_la; p.first = token; p.arg = xla;
  }

  static final class JJCalls {
    int gen;
    Token first;
    int arg;
    JJCalls next;
  }

}
