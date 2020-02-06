package rs.ac.bg.etf.js150411d.linda.util;

import java.io.*;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.StringTokenizer;

/**
 * Reprezentacija torki
 *
 * @author js150411d.etf.bg.ac.rs
 */

public class Tuple extends LinkedList<Serializable> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Pravi novu torku
     * Primer:
     * new Tuple (4, 5, "abc", true) -> [4 5 "abc" true]
     * new Tuple (4, Integer.class, "abc".getClass(), Boolean.class) -> [ 4 ?Integer ?Sting ?Boolean]
     */
    public Tuple(Serializable... components) {
        for (var comp : components) {
            this.add(comp);
        }
    }

    private static boolean matches(Serializable thisComponent, Serializable templateComponent) {
        if(templateComponent.equals("null")){
            return true;
        }
        if (templateComponent instanceof Tuple) {
            if (!(thisComponent instanceof Tuple)) {
                return false;
            } else {
                return ((Tuple) thisComponent).matches((Tuple) templateComponent);
            }
        } else if (templateComponent instanceof Class) {
            if (thisComponent instanceof Class) {
                return ((Class<?>) templateComponent).isAssignableFrom((Class<?>) thisComponent);
            } else {
                return ((Class<?>) templateComponent).isInstance(thisComponent);
            }
        } else {
            return thisComponent.equals(templateComponent);
        }
    }

    /**
     * Vraca true ako se torka poklapa sa datim templejtom
     * Pravila poklapanja : torka se poklapa sa templejtom ako i samo ako sve njene komponente se poklapaju dva prema dva
     * Poklapanje dve komponente:
     * - ako su obe vrednosti jednake
     * - ako je templejt komponenta klasa/interfejs, i ako je komponenta torke instanca/implementacija te klase/interfejsa (Class.isInstance);
     * - ako je templejt komponenta klasa/interfejs, i ako je komponenta torke podklasa/podinterfejs te klase/interfejsa (Class.isAssignableFrom);
     * - rekurzivno ako su obe komponente torke
     *
     * @param template
     * @return boolean
     */

    public boolean matches(Tuple template) {
        if (this.size() != template.size()) {
            return false;
        }
        Iterator<Serializable> itThis = this.iterator();
        Iterator<Serializable> itTemplate = template.iterator();
        while (itThis.hasNext()) {
            Serializable oThis = itThis.next();
            Serializable oTemplate = itTemplate.next();
            if (!matches(oThis, oTemplate)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Vraca true ako torka (gledano kao templejt ) sadrzi torku
     */
    public boolean contains(Tuple template) {
        return template.matches(this);
    }

    /**
     * @return duboku kopiju objekta
     */

    public Tuple deep_clone() {
        Tuple copy = null;
        try {
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(buf);
            out.writeObject(this);
            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(buf.toByteArray()));
            copy = (Tuple) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return copy;
    }

    /**
     * @return string reprezentaciju od torke
     */

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (var o : this) {
            if (o instanceof Class) {
                sb.append(" ?" + ((Class<?>) o).getName());
            } else if (o instanceof String) {
                sb.append(" \"" + o + "\"");
            } else if (o instanceof Character) {
                sb.append(" '" + o + "'");
            } else {
                sb.append(" " + o.toString());
            }
        }
        sb.append(" ]");
        return sb.toString();
    }

    public String[] toStringArray() {
        String[] sa = new String[this.size()];
        var i = 0;
        for (var o : this) {
            sa[i++] = o.toString();
        }
        return sa;
    }

    public static String arrayOfStringToString(String [] sa) {
        StringBuilder sb = new StringBuilder();
        sb.append("[ ");
        String str = Arrays.toString(sa);
        str = str.substring(1, str.length()-1).replace(",", " ");
        sb.append(str);
        sb.append(" ]");
        return sb.toString();
    }

    /**
     * Parsira sekvencu reci iz torke, inicijalni [ mora da fali
     */

    private static Tuple valueOf(StringTokenizer stk) throws TupleFormatException {
        Tuple res = new Tuple();
        while (stk.hasMoreTokens()) {
            String token = stk.nextToken();
            if (token.equals("]")) {
                return res;
            }
            if (token.startsWith("\"") && token.endsWith("\"")) {
                String val = token.substring(1, token.length() - 1);
                res.add(val);
            } else if (token.startsWith("'") && token.endsWith("'") && (token.length() > 2)) {
                res.add(new Character(token.charAt(1)));
            } else if (token.startsWith("?")) {
                String className = token.substring(1);
                Class<?> c = null;
                final String[] prefixes = {"", "java.lang.", "linda."};
                for (String prefix : prefixes) {
                    try {
                        c = Class.forName(prefix + className);
                        break;
                    } catch (ClassNotFoundException e) {
                        //Ignor and try next prefix
                    }
                }
                if (c != null) {
                    res.add(c);
                } else {
                    throw new TupleFormatException("Unknown class ?" + className);
                }
            } else if (("-0123456789".indexOf(token.charAt(0)) != -1) && (!token.contains(".")))  {
                int val;
                try {
                    val = Integer.valueOf(token);
                } catch (NumberFormatException e) {
                    throw new TupleFormatException("NumberFormatException on '" + token + "'");
                }
                res.add(val);
            } else if (("-0123456789".indexOf(token.charAt(0)) != -1) && (token.contains("."))) {
                double val;
                try {
                    val = Double.valueOf(token);
                } catch (NumberFormatException e) {
                    throw new TupleFormatException("NumberFormatException on '" + token + "'");
                }
                res.add(val);
            }else if (token.equals("true")) {
                res.add(true);
            } else if (token.equals("false")) {
                res.add(false);
            } else if (token.equals("[")) {
                Tuple val = valueOf(stk);
                res.add(val);
            } else {
                res.add(token);
               // throw new TupleFormatException("Unhandled chars : '" + token + "'");
            }
        }
        throw new TupleFormatException("Missing closing ']'");
    }


    /**
     * Vraca torku sa vrednoscu prestavljenu preko specificiranog Stringa
     * Poznate vrednosti : integer (32, -53), boolean (true, false), string ("popo"), Class Name (?Integer), rekurzivna torka
     * Primer [3, 4], [?Integer 'popo' true 78 ?Boolean], [?Integer ?Tuple], [[true 78] [ 3 4 [ 5 6 ] 7 ] ]
     * Za ove komponente , parasabilni stringovi su identicni stampanim stringovima
     * Note: Ne ocekujte da parser bude otporan na random string kao ulaz
     *
     * @param s
     * @return Objekat Torke koja sadrzi reperentaciju prednosti prosledjene u string arg
     * @throws TupleFormatException
     */
    public static Tuple valueOf(String s) throws TupleFormatException {
        StringTokenizer stk = new StringTokenizer(s);
        if (!stk.hasMoreTokens() || !stk.nextToken().equals("[")) {
            throw new TupleFormatException("Missing initial '['");
        }
        Tuple res = valueOf(stk);
        if (stk.hasMoreTokens()) {
            throw new TupleFormatException("Lingering chars after ']'");
        }
        return res;
    }
}
