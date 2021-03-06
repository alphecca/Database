/* Generated By:JavaCC: Do not edit this line. DBMSParser.java */
//package
import dbms.*;

//import
import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;
import java.io.UnsupportedEncodingException;


public class DBMSParser implements DBMSParserConstants {

  public static void main(String args[]) //throws ParseException
  {
        DBMSParser parser = new DBMSParser(System.in);

    while (true) {
      try
      {
        System.out.print("DB_2015-10033> ");
        parser.command();
      }
      catch (ParseException e) {//Syntax Error
        //System.out.println(e);
        System.out.println("Syntax Error");
        parser = new DBMSParser(System.in);
        //DBMSParser.ReInit(System.in);                
      }catch (Exception e) {
        parser = new DBMSParser(System.in);
      }
    }
  }

  final public void command() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case CREATE:
    case DROP:
    case SHOW:
    case DESC:
    case SELECT:
    case INSERT:
    case DELETE:
    case ALTER:
      queryList();
      break;
    case EXIT:
      jj_consume_token(EXIT);
      jj_consume_token(SEMICOLON);
                System.exit(0);
      break;
    default:
      jj_la1[0] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  final public void queryList() throws ParseException {
  QueryTemplate q;
    label_1:
    while (true) {
      q = query();
      jj_consume_token(SEMICOLON);
       try {
                q.executeQuery();
                System.out.print("DB_2015-10033> ");
        }catch(dbException e) {
         }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case CREATE:
      case DROP:
      case SHOW:
      case DESC:
      case SELECT:
      case INSERT:
      case DELETE:
      case ALTER:
        ;
        break;
      default:
        jj_la1[1] = jj_gen;
        break label_1;
      }
    }
  }

  final public QueryTemplate query() throws ParseException {
  QueryTemplate result = new QueryTemplate();
  int q =0 ;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case CREATE:
      result = createTableQuery();
      break;
    case DROP:
      result = dropTableQuery();
      break;
    case DESC:
      result = descQuery();
      break;
    case SHOW:
      result = showTablesQuery();
      break;
    case SELECT:
      result = selectQuery();
      break;
    case INSERT:
      result = insertQuery();
      break;
    case DELETE:
      result = deleteQuery();
      break;
    case ALTER:
      alterTableQuery();
                               result.setQueryTemplate(4);
      break;
    default:
      jj_la1[2] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    {if (true) return result;}
    throw new Error("Missing return statement in function");
  }

  final public QueryTemplate createTableQuery() throws ParseException {
  QueryTemplate result = new QueryTemplate();
  String tbn;
  createTableQuery q = new createTableQuery();
    jj_consume_token(CREATE);
    jj_consume_token(TABLE);
    tbn = tableName();
                      q.setTableName(tbn);
    tableElementList(q);
   result.setQueryTemplate(5);
   result.setQueryTemplate(q);
   {if (true) return result;}
    throw new Error("Missing return statement in function");
  }

  final public void tableElementList(createTableQuery q) throws ParseException {
    jj_consume_token(LEFT_PAREN);
    tableElement(q);
    label_2:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case COMMA:
        ;
        break;
      default:
        jj_la1[3] = jj_gen;
        break label_2;
      }
      jj_consume_token(COMMA);
      tableElement(q);
    }
    jj_consume_token(RIGHT_PAREN);
  }

  final public void tableElement(createTableQuery q) throws ParseException {
  colAttribute colAtt;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case LEGAL_IDENTIFIER:
      colAtt = columnDefinition();
    try {
            q.addColumnList(colAtt);
          }catch(Exception e) {

          }
      break;
    case PRIMARY:
    case FOREIGN:
      tableConstraintDefinition(q);
      break;
    default:
      jj_la1[4] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  final public colAttribute columnDefinition() throws ParseException {
  String colName, dataType;
  boolean nullable = true;
    colName = columnName();
    dataType = dataType();
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case NOT:
      jj_consume_token(NOT);
      jj_consume_token(NULL);
      nullable = false;
      break;
    default:
      jj_la1[5] = jj_gen;
      ;
    }
    {if (true) return new colAttribute(colName, dataType, nullable);}
    throw new Error("Missing return statement in function");
  }

  final public void tableConstraintDefinition(createTableQuery q) throws ParseException {
  ArrayList<String > pkC = new ArrayList<String >();
  refConstraint fkC;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case PRIMARY:
      //
        pkC = primaryKeyConstraint();
    try {
        q.addPkConstraint(pkC);
        }catch(Exception e) {
         ;
        }
      break;
    case FOREIGN:
      fkC = referentialConstraint();
    try {
        q.addRefConstraint(fkC);
        }catch(Exception e) {
    ;
  }
      break;
    default:
      jj_la1[6] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  final public ArrayList<String > primaryKeyConstraint() throws ParseException {
 ArrayList<String > pkList = new ArrayList<String >();
    jj_consume_token(PRIMARY);
    jj_consume_token(KEY);
    pkList = columnNameList();
    {if (true) return pkList;}
    throw new Error("Missing return statement in function");
  }

  final public refConstraint referentialConstraint() throws ParseException {
  ArrayList<String > colList = new ArrayList<String >();
  String refTbn;
  ArrayList<String > refList = new ArrayList<String >();
    jj_consume_token(FOREIGN);
    jj_consume_token(KEY);
    colList = columnNameList();
    jj_consume_token(REFERENCES);
    refTbn = tableName();
    refList = columnNameList();
    {if (true) return new refConstraint(colList, refTbn, refList);}
    throw new Error("Missing return statement in function");
  }

  final public ArrayList<String > columnNameList() throws ParseException {
  ArrayList<String > result = new ArrayList<String >();
  String tmp;
    jj_consume_token(LEFT_PAREN);
    tmp = columnName();
                       result.add(tmp);
    label_3:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case COMMA:
        ;
        break;
      default:
        jj_la1[7] = jj_gen;
        break label_3;
      }
      jj_consume_token(COMMA);
      tmp = columnName();
                         result.add(tmp);
    }
    jj_consume_token(RIGHT_PAREN);
    {if (true) return result;}
    throw new Error("Missing return statement in function");
  }

  final public String dataType() throws ParseException {
  String dataType = new String();
  Token size;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case INT:
      jj_consume_token(INT);
    dataType = "int";
      break;
    case CHAR:
      jj_consume_token(CHAR);
      jj_consume_token(LEFT_PAREN);
      size = jj_consume_token(INT_VALUE);
      jj_consume_token(RIGHT_PAREN);
    int l=0;
    try {
      l = Integer.parseInt(size.image);
      }catch(NumberFormatException e) {
          //int size bigger than 32bit
    }
        dataType = "char("+String.valueOf(l)+")";
      break;
    case DATE:
      jj_consume_token(DATE);
    dataType = "date";
      break;
    default:
      jj_la1[8] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  {if (true) return dataType;}
    throw new Error("Missing return statement in function");
  }

  final public String tableName() throws ParseException {
 Token t;
    t = jj_consume_token(LEGAL_IDENTIFIER);
    {if (true) return t.image.toLowerCase();}
    throw new Error("Missing return statement in function");
  }

  final public String columnName() throws ParseException {
  Token tkn;
    tkn = jj_consume_token(LEGAL_IDENTIFIER);
    {if (true) return tkn.image.toLowerCase();}
        // TODO (ignore case?)String result = tkn.image.toLowerCase();
        // TODO check example: create table TaBLe(coli int, colj int);

    throw new Error("Missing return statement in function");
  }

  final public QueryTemplate dropTableQuery() throws ParseException {
  QueryTemplate result = new QueryTemplate();
  String tbn;
    jj_consume_token(DROP);
    jj_consume_token(TABLE);
    tbn = tableName();
        result.setQueryTemplate(6);
        result.setQueryTemplate(tbn);
        {if (true) return result;}
    throw new Error("Missing return statement in function");
  }

  final public QueryTemplate descQuery() throws ParseException {
  QueryTemplate result = new QueryTemplate();
  String tbn;
    jj_consume_token(DESC);
    tbn = tableName();
    result.setQueryTemplate(7);
        result.setQueryTemplate(tbn);
        {if (true) return result;}
    throw new Error("Missing return statement in function");
  }

  final public QueryTemplate showTablesQuery() throws ParseException {
  QueryTemplate result = new QueryTemplate();
    jj_consume_token(SHOW);
    jj_consume_token(TABLES);
    result.setQueryTemplate(8);
        {if (true) return result;}
    throw new Error("Missing return statement in function");
  }

  final public QueryTemplate selectQuery() throws ParseException {
  QueryTemplate q = new QueryTemplate();
  ArrayList<refTabCol > sl = new ArrayList<refTabCol >();
  selectQuery sq = new selectQuery();
  q.setQueryTemplate(1);
    jj_consume_token(SELECT);
    sl = selectList();
    sq = tableExpression();
    sq.setSelectList(sl);
    q.selectQuery(sq);
    {if (true) return q;}
    throw new Error("Missing return statement in function");
  }

  final public ArrayList<refTabCol > selectList() throws ParseException {
  Token tk;
  ArrayList<refTabCol > result = new ArrayList<refTabCol >();
  refTabCol tmp = new refTabCol();
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case STAR:
      tk = jj_consume_token(STAR);
                    tmp.setIsRef(true); tmp.setC(tk.image); tmp.setT(tk.image); result.add(tmp);
      break;
    case COUNT:
    case SUM:
    case AVG:
    case LEGAL_IDENTIFIER:
      tmp = selectedColumn();
                                     result.add(tmp);
      label_4:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case COMMA:
          ;
          break;
        default:
          jj_la1[9] = jj_gen;
          break label_4;
        }
        jj_consume_token(COMMA);
        tmp = selectedColumn();
                                                 result.add(tmp);
      }
      break;
    default:
      jj_la1[10] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
         {if (true) return result;}
    throw new Error("Missing return statement in function");
  }

  final public selectQuery tableExpression() throws ParseException {
  selectQuery result = new selectQuery();
  ArrayList<refTabCol > al = new ArrayList<refTabCol >();
  ExpTree tree = new ExpTree();
    al = fromClause();
                      result.setFromList(al);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case WHERE:
      tree = whereClause();
                           result.setWhereClause(tree);
      break;
    default:
      jj_la1[11] = jj_gen;
      ;
    }
   {if (true) return result;}
    throw new Error("Missing return statement in function");
  }

  final public refTabCol selectedColumn() throws ParseException {
  Token tk = new Token();
  refTabCol result = new refTabCol();
  String tmp = new String();
  String alias = new String();
  String col = new String();
  String tbn = new String();
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case LEGAL_IDENTIFIER:
      tk = jj_consume_token(LEGAL_IDENTIFIER);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case PERIOD:
        jj_consume_token(PERIOD);
        col = columnName();
        break;
      default:
        jj_la1[12] = jj_gen;
        ;
      }
      break;
    case COUNT:
      count();
      break;
    case SUM:
      sum();
      break;
    case AVG:
      avg();
      break;
    default:
      jj_la1[13] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case AS:
      jj_consume_token(AS);
      alias = columnName();
      break;
    default:
      jj_la1[14] = jj_gen;
      ;
    }
    result.setA(alias);
    tmp = tk.image;
    if(col == null || col.isEmpty()) col = tmp;
    else tbn = tmp;
    result.setT(tbn);
    result.setC(col);
        {if (true) return result;}
    throw new Error("Missing return statement in function");
  }

  final public void count() throws ParseException {
    jj_consume_token(COUNT);
    jj_consume_token(LEFT_PAREN);
    jj_consume_token(LEGAL_IDENTIFIER);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case PERIOD:
      jj_consume_token(PERIOD);
      columnName();
      break;
    default:
      jj_la1[15] = jj_gen;
      ;
    }
    jj_consume_token(RIGHT_PAREN);
  }

  final public void sum() throws ParseException {
    jj_consume_token(SUM);
    jj_consume_token(LEFT_PAREN);
    jj_consume_token(LEGAL_IDENTIFIER);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case PERIOD:
      jj_consume_token(PERIOD);
      columnName();
      break;
    default:
      jj_la1[16] = jj_gen;
      ;
    }
    jj_consume_token(RIGHT_PAREN);
  }

  final public void avg() throws ParseException {
    jj_consume_token(AVG);
    jj_consume_token(LEFT_PAREN);
    jj_consume_token(LEGAL_IDENTIFIER);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case PERIOD:
      jj_consume_token(PERIOD);
      columnName();
      break;
    default:
      jj_la1[17] = jj_gen;
      ;
    }
    jj_consume_token(RIGHT_PAREN);
  }

  final public ArrayList<refTabCol > fromClause() throws ParseException {
  ArrayList<refTabCol > result = new ArrayList<refTabCol >();
    jj_consume_token(FROM);
    result = tableReferenceList();
    {if (true) return result;}
    throw new Error("Missing return statement in function");
  }

  final public ExpTree whereClause() throws ParseException {
  ExpTree result = new ExpTree();
    jj_consume_token(WHERE);
    result = booleanValueExpression();
    {if (true) return result;}
    throw new Error("Missing return statement in function");
  }

  final public ArrayList<refTabCol > tableReferenceList() throws ParseException {
  ArrayList<refTabCol > result = new ArrayList<refTabCol >();
  refTabCol tmp = new refTabCol();
    tmp = referedTable();
     result.add(tmp);
    label_5:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case COMMA:
        ;
        break;
      default:
        jj_la1[18] = jj_gen;
        break label_5;
      }
      jj_consume_token(COMMA);
      tmp = referedTable();
      result.add(tmp);
    }
    {if (true) return result;}
    throw new Error("Missing return statement in function");
  }

  final public refTabCol referedTable() throws ParseException {
  refTabCol result = new refTabCol();
  String tbn = new String();
  String alias = new String();
    tbn = tableName();
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case AS:
      jj_consume_token(AS);
      alias = tableName();
      break;
    default:
      jj_la1[19] = jj_gen;
      ;
    }
    result.setTA(tbn, alias);
    {if (true) return result;}
    throw new Error("Missing return statement in function");
  }

  final public ExpTree booleanValueExpression() throws ParseException {
  ExpTree result = new ExpTree();
  ArrayList<ExpTree > child = new ArrayList<ExpTree >();
  ExpTree tmp = new ExpTree();
  result.setOperator("or");
    tmp = booleanTerm();
                        child.add(tmp);
    label_6:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case OR:
        ;
        break;
      default:
        jj_la1[20] = jj_gen;
        break label_6;
      }
      jj_consume_token(OR);
      tmp = booleanTerm();
                        child.add(tmp);
    }
    result.setChild(child);
    {if (true) return result;}
    throw new Error("Missing return statement in function");
  }

  final public ExpTree booleanTerm() throws ParseException {
  ExpTree result = new ExpTree();
  ArrayList<ExpTree > child = new ArrayList<ExpTree >();
  ExpTree tmp = new ExpTree();
  result.setOperator("and");
    tmp = booleanFactor();
                         child.add(tmp);
    label_7:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case AND:
        ;
        break;
      default:
        jj_la1[21] = jj_gen;
        break label_7;
      }
      jj_consume_token(AND);
      tmp = booleanFactor();
                                child.add(tmp);
    }
    result.setChild(child);
    {if (true) return result;}
    throw new Error("Missing return statement in function");
  }

  final public ExpTree booleanFactor() throws ParseException {
  Token tk = new Token();
  ExpTree tmp = new ExpTree();
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case NOT:
      tk = jj_consume_token(NOT);
      break;
    default:
      jj_la1[22] = jj_gen;
      ;
    }
    tmp = booleanTest();
    String foo = tk.image;
    if(foo != null){// negation needed
        ExpTree result = new ExpTree();
        ArrayList<ExpTree > child = new ArrayList<ExpTree >();

                child.add(tmp);
                result.setChild(child);
                result.setOperator("not");
                {if (true) return result;}
        }else {
                {if (true) return tmp;}
        }
    throw new Error("Missing return statement in function");
  }

  final public ExpTree booleanTest() throws ParseException {
  ExpTree result = new ExpTree();
  ExpTree tmp = new ExpTree();
  predicate op = new predicate();
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case INT_VALUE:
    case LEGAL_IDENTIFIER:
    case CHAR_STRING:
    case DATE_VALUE:
      op = predicate();
                             if(op!=null) result.setNode(op);
      break;
    case LEFT_PAREN:
      tmp = parenthesizedBooleanExpression();
                  if(tmp!= null)
                  result = tmp;
      break;
    default:
      jj_la1[23] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
          {if (true) return result;}
    throw new Error("Missing return statement in function");
  }

  final public predicate predicate() throws ParseException {
  predicate result = new predicate();
  Token tk;
  String tmp= new String();
  String operator  = new String();
  refTabCol Op1 = new refTabCol();
  refTabCol Op2 = new refTabCol();
  String tbn = new String();
  String col = new String();
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case LEGAL_IDENTIFIER:
      tk = jj_consume_token(LEGAL_IDENTIFIER);
                                        tmp = tk.image;
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case PERIOD:
        jj_consume_token(PERIOD);
        col = columnName();
        break;
      default:
        jj_la1[24] = jj_gen;
        ;
      }
              if(col.isEmpty()) {
                 col = tmp;

                 Op1.setTC(tbn, col);
               }
              else {
                 tbn = tmp;
                 Op1.setTC(tbn, col);

               }

              Op1.setIsRef(true);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case IS:
        operator = nullOperation();
        break;
      case LEFT_PEAK:
      case RIGHT_PEAK:
      case EQUAL:
      case 59:
      case 60:
      case 61:
        operator = compOp();
        Op2 = compOperand();
        break;
      default:
        jj_la1[25] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    result.setOperator(operator);
    result.setOperand(Op1, Op2);

    {if (true) return result;}
      break;
    case INT_VALUE:
    case CHAR_STRING:
    case DATE_VALUE:
      tmp = comparableValue();
      operator = compOp();
      Op2 = compOperand();
      Op1.setC(tmp);
      Op1.setIsRef(false);
      result.setOperand(Op1, Op2);
      result.setOperator(operator);

      {if (true) return result;}
      break;
    default:
      jj_la1[26] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  final public ExpTree parenthesizedBooleanExpression() throws ParseException {
  ExpTree result = new ExpTree();
    jj_consume_token(LEFT_PAREN);
    result = booleanValueExpression();
    jj_consume_token(RIGHT_PAREN);
    {if (true) return result;}
    throw new Error("Missing return statement in function");
  }

  final public predicate comparisonPredicate() throws ParseException {
  refTabCol op1, op2;
  String operator;
    op1 = compOperand();
    operator = compOp();
    op2 = compOperand();
    {if (true) return new predicate(op1, operator, op2);}
    throw new Error("Missing return statement in function");
  }

  final public refTabCol compOperand() throws ParseException {
  refTabCol result = new refTabCol();
  Token tk;
  String tmp = new String();
  String col = new String();
  String tbn = new String();
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case INT_VALUE:
    case CHAR_STRING:
    case DATE_VALUE:
      col = comparableValue();
          result.setC(col);
          result.setIsRef(false);

          {if (true) return result;}
      break;
    case LEGAL_IDENTIFIER:
      tk = jj_consume_token(LEGAL_IDENTIFIER);
                                tmp = tk.image;
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case PERIOD:
        jj_consume_token(PERIOD);
        col = columnName();
        break;
      default:
        jj_la1[27] = jj_gen;
        ;
      }
          if(col.isEmpty()) col = tmp;
          else tbn = tmp;
          result.setC(col);
          result.setT(tbn);
          result.setIsRef(true);

          {if (true) return result;}
      break;
    default:
      jj_la1[28] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  final public String compOp() throws ParseException {
  Token tk;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case LEFT_PEAK:
    case RIGHT_PEAK:
    case EQUAL:
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case LEFT_PEAK:
        tk = jj_consume_token(LEFT_PEAK);
        break;
      case RIGHT_PEAK:
        tk = jj_consume_token(RIGHT_PEAK);
        break;
      case EQUAL:
        tk = jj_consume_token(EQUAL);
        break;
      default:
        jj_la1[29] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
        {if (true) return tk.image;}
      break;
    case 59:
    case 60:
    case 61:
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 59:
        tk = jj_consume_token(59);
        break;
      case 60:
        tk = jj_consume_token(60);
        break;
      case 61:
        tk = jj_consume_token(61);
        break;
      default:
        jj_la1[30] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
      {if (true) return tk.image;}
      break;
    default:
      jj_la1[31] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  final public String comparableValue() throws ParseException {
  String result = new String();
  Token tmp;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case INT_VALUE:
      tmp = jj_consume_token(INT_VALUE);
      break;
    case CHAR_STRING:
      tmp = jj_consume_token(CHAR_STRING);
      break;
    case DATE_VALUE:
      tmp = jj_consume_token(DATE_VALUE);
      break;
    default:
      jj_la1[32] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
   result = tmp.image;
   {if (true) return result;}
    throw new Error("Missing return statement in function");
  }

  final public predicate nullPredicate() throws ParseException {
  predicate result = new predicate();
  refTabCol op1 = new refTabCol();
  String tmp = new String();
  Token tk = new Token();
  String operator;
    tk = jj_consume_token(LEGAL_IDENTIFIER);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case PERIOD:
      jj_consume_token(PERIOD);
      tmp = columnName();
      break;
    default:
      jj_la1[33] = jj_gen;
      ;
    }
    if(tmp == null | tmp.isEmpty())
        op1.setC(tk.image);
    else
        op1.setCA(tk.image, tmp);
    op1.setIsRef(true);
    operator = nullOperation();
    result.setOperand(op1, null);
    result.setOperator(operator);
    {if (true) return result;}
    throw new Error("Missing return statement in function");
  }

  final public String nullOperation() throws ParseException {
  Token tk;
  String result="is";
    jj_consume_token(IS);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case NOT:
      tk = jj_consume_token(NOT);
                  result+=" not";
      break;
    default:
      jj_la1[34] = jj_gen;
      ;
    }
    jj_consume_token(NULL);
    result += " null";
    {if (true) return result;}
    throw new Error("Missing return statement in function");
  }

  final public QueryTemplate insertQuery() throws ParseException {
  String tbn;
        QueryTemplate q = new QueryTemplate();
        insertQuery iq = new insertQuery();
        insertQuery values = new insertQuery();
    jj_consume_token(INSERT);
    jj_consume_token(INTO);
    tbn = tableName();
    values = insertColumnsAndSources();
    iq = values;
    iq.setTbn(tbn);
    q.setInsertQuery(iq);
    q.setQueryTemplate(2);
    {if (true) return q;}
    throw new Error("Missing return statement in function");
  }

  final public insertQuery insertColumnsAndSources() throws ParseException {
  insertQuery result = new insertQuery();
  ArrayList<String > colFam = new ArrayList<String >();
  tupleElement valL = new tupleElement();
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case LEFT_PAREN:
      colFam = columnNameList();
      break;
    default:
      jj_la1[35] = jj_gen;
      ;
    }
    valL = valueList();
     result.setColumnFamily(colFam);
     result.setTupleElement(valL);
     {if (true) return result;}
    throw new Error("Missing return statement in function");
  }

  final public tupleElement valueList() throws ParseException {
  String tmp = new String();
  ArrayList<String > valL = new ArrayList<String >();
    jj_consume_token(VALUES);
    jj_consume_token(LEFT_PAREN);
    tmp = value();
                  valL.add(tmp);
    label_8:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case COMMA:
        ;
        break;
      default:
        jj_la1[36] = jj_gen;
        break label_8;
      }
      jj_consume_token(COMMA);
      tmp = value();
                    valL.add(tmp);
    }
    jj_consume_token(RIGHT_PAREN);
    {if (true) return new tupleElement(valL);}
    throw new Error("Missing return statement in function");
  }

  final public String value() throws ParseException {
  String result = new String("null");
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case NULL:
      jj_consume_token(NULL);
      break;
    case INT_VALUE:
    case CHAR_STRING:
    case DATE_VALUE:
      result = comparableValue();
      break;
    default:
      jj_la1[37] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    {if (true) return result;}
    throw new Error("Missing return statement in function");
  }

  final public QueryTemplate deleteQuery() throws ParseException {
  QueryTemplate q = new QueryTemplate();
  deleteQuery dq = new deleteQuery();
  String tbn = new String();
  ExpTree tree = new ExpTree();
    jj_consume_token(DELETE);
    jj_consume_token(FROM);
    tbn = tableName();
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case WHERE:
      tree = whereClause();
      break;
    default:
      jj_la1[38] = jj_gen;
      ;
    }
    dq.setDeleteQuery(tbn, tree);
    q.setDeleteQuery(dq);
    q.setQueryTemplate(3);
    {if (true) return q;}
    throw new Error("Missing return statement in function");
  }

/*****Optional*****/
  final public void alterTableQuery() throws ParseException {
    jj_consume_token(ALTER);
    jj_consume_token(TABLE);
    tableName();
    jj_consume_token(ADD);
    columnName();
    dataType();
  }

  /** Generated Token Manager. */
  public DBMSParserTokenManager token_source;
  SimpleCharStream jj_input_stream;
  /** Current token. */
  public Token token;
  /** Next token. */
  public Token jj_nt;
  private int jj_ntk;
  private int jj_gen;
  final private int[] jj_la1 = new int[39];
  static private int[] jj_la1_0;
  static private int[] jj_la1_1;
  static {
      jj_la1_init_0();
      jj_la1_init_1();
   }
   private static void jj_la1_init_0() {
      jj_la1_0 = new int[] {0x90360060,0x90360040,0x90360040,0x0,0x1400,0x100,0x1400,0x0,0x1c000,0x0,0x0,0x1000000,0x0,0x0,0x400000,0x0,0x0,0x0,0x0,0x400000,0x2000000,0x4000000,0x100,0x0,0x0,0x8000000,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x100,0x0,0x0,0x200,0x1000000,};
   }
   private static void jj_la1_init_1() {
      jj_la1_1 = new int[] {0x1,0x1,0x1,0x200,0x20000,0x0,0x0,0x200,0x0,0x200,0x82001c,0x0,0x100,0x2001c,0x0,0x100,0x100,0x100,0x200,0x0,0x0,0x0,0x0,0x4a4040,0x100,0x39003000,0x4a4000,0x100,0x4a4000,0x1003000,0x38000000,0x39003000,0x484000,0x100,0x0,0x40,0x200,0x484000,0x0,};
   }

  /** Constructor with InputStream. */
  public DBMSParser(java.io.InputStream stream) {
     this(stream, null);
  }
  /** Constructor with InputStream and supplied encoding */
  public DBMSParser(java.io.InputStream stream, String encoding) {
    try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source = new DBMSParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 39; i++) jj_la1[i] = -1;
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
    for (int i = 0; i < 39; i++) jj_la1[i] = -1;
  }

  /** Constructor. */
  public DBMSParser(java.io.Reader stream) {
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new DBMSParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 39; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 39; i++) jj_la1[i] = -1;
  }

  /** Constructor with generated Token Manager. */
  public DBMSParser(DBMSParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 39; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(DBMSParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 39; i++) jj_la1[i] = -1;
  }

  private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
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

  /** Generate ParseException. */
  public ParseException generateParseException() {
    jj_expentries.clear();
    boolean[] la1tokens = new boolean[62];
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 39; i++) {
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
    for (int i = 0; i < 62; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.add(jj_expentry);
      }
    }
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

}
