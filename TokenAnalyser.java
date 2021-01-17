import java.io.IOException;
import java.util.*;

public class TokenAnalyser {
    String text;
    ArrayList<Token> tokens;
    int start;
    int cur;
    private static final HashMap<String, TokenType> keyword = new HashMap<>();

    static {
        keyword.put("fn", TokenType.FN_KW);
        keyword.put("let", TokenType.LET_KW);
        keyword.put("const", TokenType.CONST_KW);
        keyword.put("as", TokenType.AS_KW);
        keyword.put("while", TokenType.WHILE_KW);
        keyword.put("if", TokenType.IF_KW);
        keyword.put("else", TokenType.ELSE_KW);
        keyword.put("return", TokenType.RETURN_KW);
        keyword.put("break", TokenType.BREAK_KW);
        keyword.put("continue", TokenType.CONTINUE_KW);
    }

    static void analyseTokens(String s,String dest) throws IOException {
        TokenAnalyser a = new TokenAnalyser(s);
        a.readTokens();
        for (Token token : a.tokens) {
            System.out.println(token);
        }

        Parser p=new Parser(a.tokens);
        p.startP(dest);
        //
    }

    void readTokens() {
        while (text.charAt(cur) != '&') {
            start = cur;
            nextToken();
        }
        tokens.add(new Token(TokenType.EOF, ""));
    }

    void nextToken() {
        char c = nextChar();
        switch (c) {
            case '+':
                tokens.add(new Token(TokenType.PLUS, "+"));
                break;
            //case '-':tokens.add(new Token(TokenType.MINUS,"-")); break;
            case '*':
                tokens.add(new Token(TokenType.MUL, "*"));
                break;
            //case '/':tokens.add(new Token(TokenType.DIV,"/")); break;
            case '(':
                tokens.add(new Token(TokenType.L_PAREN, "("));
                break;
            case ')':
                tokens.add(new Token(TokenType.R_PAREN, ")"));
                break;
            case '{':
                tokens.add(new Token(TokenType.L_BRACE, "{"));
                break;
            case '}':
                tokens.add(new Token(TokenType.R_BRACE, "}"));
                break;
            case ',':
                tokens.add(new Token(TokenType.COMMA, ","));
                break;
            case ':':
                tokens.add(new Token(TokenType.COLON, ":"));
                break;
            case ';':
                tokens.add(new Token(TokenType.SEMICOLON, ";"));
                break;
            case '-':
                if (text.charAt(cur) == '>') {
                    nextChar();
                    tokens.add(new Token(TokenType.ARROW, "->"));
                } else {
                    tokens.add(new Token(TokenType.MINUS, "-"));
                }
                break;
            case '=':
                if (text.charAt(cur) == '=') {
                    nextChar();
                    tokens.add(new Token(TokenType.EQ, "=="));
                } else {
                    tokens.add(new Token(TokenType.ASSIGN, "="));
                }
                break;
            case '<':
                if (text.charAt(cur) == '=') {
                    nextChar();
                    tokens.add(new Token(TokenType.LE, "<="));
                } else {
                    tokens.add(new Token(TokenType.LT, "<"));
                }
                break;
            case '>':
                if (text.charAt(cur) == '=') {
                    nextChar();
                    tokens.add(new Token(TokenType.GE, ">="));
                } else {
                    tokens.add(new Token(TokenType.GT, ">"));
                }
                break;
            case '!':
                if (text.charAt(cur) == '=') {
                    nextChar();
                    tokens.add(new Token(TokenType.NEQ, "!="));
                } else {
                    System.exit(4);
                }
                break;
            case '/':
                if (text.charAt(cur) == '/') {
                    c = nextChar();
                    while (c != '&' && c != '\n') {
                        c = nextChar();
                    }
                } else {
                    tokens.add(new Token(TokenType.DIV, "/"));
                }
            case ' ':
            case '\r':
            case '\t':
            case '\n':
                break;
            case '\"':
                analyseString();
                break;
            case '\'':
                if (text.charAt(cur) != '\'' && text.charAt(cur) != '\\') {
                    cur++;
                    if (text.charAt(cur) == '\'') {
                        tokens.add(new Token(TokenType.CHAR_LITERAL, text.charAt(cur - 1)));
                        cur++;
                        break;
                    } else {
                        System.exit(7);
                    }
                } else if (text.charAt(cur) == '\\') {
                    char tmp;
                    cur++;
                    if(text.charAt(cur)=='n'&&text.charAt(cur+1) == '\''){
                        cur+=2;
                        tmp='\n';
                        tokens.add(new Token(TokenType.CHAR_LITERAL,tmp));
                        break;
                    }else if(text.charAt(cur)=='r'&&text.charAt(cur+1) == '\''){
                        cur+=2;
                        tmp='\r';
                        tokens.add(new Token(TokenType.CHAR_LITERAL,tmp));
                        break;
                    }else if(text.charAt(cur)=='t'&&text.charAt(cur+1) == '\''){
                        cur+=2;
                        tmp='\t';
                        tokens.add(new Token(TokenType.CHAR_LITERAL,tmp));
                        break;
                    }else if(text.charAt(cur)=='\\'&&text.charAt(cur+1) == '\''){
                        cur+=2;
                        tmp='\\';
                        tokens.add(new Token(TokenType.CHAR_LITERAL,tmp));
                        break;
                    }else if(text.charAt(cur)=='\''&&text.charAt(cur+1) == '\''){
                        cur+=2;
                        tmp='\'';
                        tokens.add(new Token(TokenType.CHAR_LITERAL,tmp));
                        break;
                    }else if(text.charAt(cur)=='\"'&&text.charAt(cur+1) == '\''){
                        cur+=2;
                        tmp='\"';
                        tokens.add(new Token(TokenType.CHAR_LITERAL,tmp));
                        break;
                    }else{
                        System.exit(7);
                    }
                } else {
                    System.exit(7);
                }
            default:
                if (isDigit(c)) {
                    analyseNumber();
                } else if (isAlpha(c)) {
                    analyseIdentifier();
                } else {
                    System.exit(5);
                }
        }
    }

    private char nextChar() {
        cur++;
        return text.charAt(cur - 1);
    }

    private void analyseString() {
        StringBuilder tmp;
        tmp = new StringBuilder();
        while (text.charAt(cur) != '\"' && text.charAt(cur) != '&') {
            if (text.charAt(cur) == '\\') {
                if(text.charAt(cur+1)=='n'){
                    cur+=2;
                    tmp.append('\n');
                }else if(text.charAt(cur+1)=='r'){
                    cur+=2;
                    tmp.append('\r');
                }else if(text.charAt(cur+1)=='t'){
                    cur+=2;
                    tmp.append('\t');
                }else if(text.charAt(cur+1)=='\\'){
                    cur+=2;
                    tmp.append('\\');
                }else if(text.charAt(cur+1)=='\''){
                    cur+=2;
                    tmp.append('\'');
                }else if(text.charAt(cur+1)=='\"'){
                    cur+=2;
                    tmp.append('\"');
                }else{
                    System.exit(8);
                }
            }
            else{
                tmp.append(text.charAt(cur));
                cur++;
            }
        }
        if (text.charAt(cur) == '&') {
            System.exit(8);
        } else {
            cur++;
            //System.out.println(tmp);
            tokens.add(new Token(TokenType.STRING_LITERAL, tmp.toString()));
        }
    }

    private void analyseNumber() {
        String tmp;
        while (isDigit(text.charAt(cur))) {
            cur++;
        }
        //System.out.println(text.charAt(cur-1));
        if (text.charAt(cur) == '.' && isDigit(text.charAt(cur + 1))) {
            cur++;
            while (isDigit(text.charAt(cur))) {
                cur++;
            }
            if (text.charAt(cur) == 'e' || text.charAt(cur) == 'E') {
                cur++;
                if (text.charAt(cur) == '+' || text.charAt(cur) == '-') {
                    cur++;
                }
                while (isDigit(text.charAt(cur))) {
                    cur++;
                }
            }
        }
        tmp = text.substring(start, cur);
        if (!isDigit(text.charAt(cur - 1))) {
            System.exit(6);
        }
        if (tmp.contains(".")) {
            tokens.add(new Token(TokenType.DOUBLE_LITERAL, Double.parseDouble(tmp)));
        } else {
                tokens.add(new Token(TokenType.UINT_LITERAL, Long.parseLong(tmp)));
        }
    }

    private void analyseIdentifier() {
        String tmp;
        while (isDigit(text.charAt(cur)) || isAlpha(text.charAt(cur))) {
            cur++;
        }
        tmp = text.substring(start, cur);
        TokenType tokenType = keyword.get(tmp);
        if(tokenType==null){
            tokens.add(new Token(TokenType.IDENT, tmp));
        }else{
            tokens.add(new Token(tokenType,tmp));
        }
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
    }


    TokenAnalyser(String s) {
        this.text = s + "&&";
        this.tokens = new ArrayList<>();
        this.cur = 0;
        this.start = 0;
    }
}

class Token {
    TokenType tokenType;
    Object tokenValue;

    Token(TokenType tokenType, Object tokenValue) {
        this.tokenType = tokenType;
        this.tokenValue = tokenValue;
    }

    public String toString() {
        return tokenType + " " + tokenValue;
    }
}

enum TokenType {
    //keyword
    FN_KW, LET_KW, CONST_KW, AS_KW, WHILE_KW, IF_KW, ELSE_KW, RETURN_KW, BREAK_KW, CONTINUE_KW,

    //literal
    UINT_LITERAL, STRING_LITERAL, DOUBLE_LITERAL, CHAR_LITERAL,

    //identifier
    IDENT,

    //operator
    PLUS, MINUS, MUL, DIV, ASSIGN, LT, GT, L_PAREN, R_PAREN, L_BRACE, R_BRACE, COMMA, COLON, SEMICOLON,
    EQ, NEQ, LE, GE, ARROW,

    //eof
    EOF
}
