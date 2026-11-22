package com.scientific.calculator;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    // ============================================================
    // الألوان (نفس نظام KlencodIDE)
    // ============================================================
    private static final int COLOR_BG          = 0xFF0D1117;
    private static final int COLOR_SURFACE     = 0xFF161B22;
    private static final int COLOR_SURFACE_ALT = 0xFF21262D;
    private static final int COLOR_PRIMARY     = 0xFF1F6FEB;
    private static final int COLOR_PRIMARY_LT  = 0xFF58A6FF;
    private static final int COLOR_TEXT        = 0xFFE6EDF3;
    private static final int COLOR_MUTED       = 0xFF8B949E;
    private static final int COLOR_BORDER      = 0xFF30363D;
    private static final int COLOR_GREEN       = 0xFF238636;
    private static final int COLOR_GREEN_LT    = 0xFF56D364;
    private static final int COLOR_RED         = 0xFFDA3633;
    private static final int COLOR_RED_LT      = 0xFFFF7B72;
    private static final int COLOR_ORANGE      = 0xFFD29922;
    private static final int COLOR_PURPLE      = 0xFF8957E5;

    // ============================================================
    // الحالة
    // ============================================================
    private TextView exprView;
    private TextView resultView;
    private TextView angleView;
    private TextView memView;

    private String expression = "";
    private double memory = 0;
    private boolean useDegrees = true;

    private final List<String> history = new ArrayList<>();
    private LinearLayout historyContainer;
    private ScrollView historyScroll;

    // ============================================================
    // dp helper
    // ============================================================
    private int dp(int v) {
        return Math.round(v * getResources().getDisplayMetrics().density);
    }

    // ============================================================
    // onCreate
    // ============================================================
    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(COLOR_BG);
        getWindow().setNavigationBarColor(COLOR_BG);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(COLOR_BG);

        // ---------- الترويسة ----------
        root.addView(buildHeader(), new LinearLayout.LayoutParams(-1, -2));

        // ---------- شاشة العرض ----------
        root.addView(buildDisplay(), new LinearLayout.LayoutParams(-1, -2));

        // ---------- سجل العمليات (قابل للطي) ----------
        root.addView(buildHistory(), new LinearLayout.LayoutParams(-1, 0, 0.35f));

        // ---------- لوحة الأزرار ----------
        root.addView(buildKeypad(), new LinearLayout.LayoutParams(-1, 0, 1.65f));

        setContentView(root);
        updateDisplay();
    }

    // ============================================================
    // الترويسة
    // ============================================================
    private View buildHeader() {
        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(dp(14), dp(10), dp(14), dp(10));
        header.setBackgroundColor(COLOR_SURFACE);

        TextView title = new TextView(this);
        title.setText("🧮 حاسبة علمية");
        title.setTextColor(COLOR_TEXT);
        title.setTextSize(17);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        header.addView(title, new LinearLayout.LayoutParams(0, -2, 1f));

        // زر DEG/RAD
        angleView = new TextView(this);
        angleView.setTextSize(12);
        angleView.setTypeface(Typeface.DEFAULT_BOLD);
        angleView.setPadding(dp(12), dp(6), dp(12), dp(6));
        angleView.setOnClickListener(v -> {
            useDegrees = !useDegrees;
            updateAngleBadge();
            updateDisplay();
        });
        header.addView(angleView);
        updateAngleBadge();

        // زر MC (مسح الذاكرة)
        TextView memClear = new TextView(this);
        memClear.setText("MC");
        memClear.setTextColor(COLOR_MUTED);
        memClear.setTextSize(12);
        memClear.setPadding(dp(10), dp(6), dp(10), dp(6));
        memClear.setOnClickListener(v -> { memory = 0; updateDisplay(); });
        header.addView(memClear);

        // شارة الذاكرة
        memView = new TextView(this);
        memView.setTextSize(11);
        memView.setTextColor(COLOR_ORANGE);
        memView.setPadding(dp(6), 0, 0, 0);
        header.addView(memView);

        return header;
    }

    private void updateAngleBadge() {
        GradientDrawable d = new GradientDrawable();
        d.setCornerRadius(dp(20));
        d.setColor(useDegrees ? COLOR_PRIMARY : COLOR_PURPLE);
        angleView.setBackground(d);
        angleView.setTextColor(Color.WHITE);
        angleView.setText(useDegrees ? "DEG" : "RAD");
    }

    // ============================================================
    // شاشة العرض
    // ============================================================
    private View buildDisplay() {
        LinearLayout wrap = new LinearLayout(this);
        wrap.setOrientation(LinearLayout.VERTICAL);
        wrap.setPadding(dp(16), dp(14), dp(16), dp(14));
        wrap.setBackgroundColor(COLOR_SURFACE);

        exprView = new TextView(this);
        exprView.setTextColor(COLOR_MUTED);
        exprView.setTextSize(18);
        exprView.setGravity(Gravity.END);
        exprView.setSingleLine(true);
        exprView.setEllipsize(TextUtils.TruncateAt.START);
        wrap.addView(exprView);

        resultView = new TextView(this);
        resultView.setTextColor(COLOR_TEXT);
        resultView.setTextSize(38);
        resultView.setTypeface(Typeface.DEFAULT_BOLD);
        resultView.setGravity(Gravity.END);
        resultView.setSingleLine(true);
        resultView.setEllipsize(TextUtils.TruncateAt.START);
        LinearLayout.LayoutParams rp = new LinearLayout.LayoutParams(-1, -2);
        rp.topMargin = dp(6);
        wrap.addView(resultView, rp);

        return wrap;
    }

    // ============================================================
    // سجل العمليات
    // ============================================================
    private View buildHistory() {
        historyScroll = new ScrollView(this);
        historyScroll.setBackgroundColor(COLOR_SURFACE_ALT);
        historyScroll.setPadding(dp(10), dp(6), dp(10), dp(6));

        historyContainer = new LinearLayout(this);
        historyContainer.setOrientation(LinearLayout.VERTICAL);
        historyScroll.addView(historyContainer);

        refreshHistory();
        return historyScroll;
    }

    private void refreshHistory() {
        historyContainer.removeAllViews();
        if (history.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("لا يوجد سجل بعد");
            empty.setTextColor(COLOR_MUTED);
            empty.setTextSize(12);
            empty.setGravity(Gravity.CENTER);
            empty.setPadding(0, dp(10), 0, dp(10));
            historyContainer.addView(empty);
            return;
        }
        for (int i = history.size() - 1; i >= 0; i--) {
            TextView tv = new TextView(this);
            tv.setText(history.get(i));
            tv.setTextColor(COLOR_TEXT);
            tv.setTextSize(13);
            tv.setPadding(dp(6), dp(4), dp(6), dp(4));
            tv.setOnClickListener(v -> {
                String h = tv.getText().toString();
                int eq = h.lastIndexOf(" = ");
                if (eq > 0) {
                    expression = h.substring(eq + 3);
                    updateDisplay();
                }
            });
            historyContainer.addView(tv);
        }
    }

    // ============================================================
    // لوحة الأزرار
    // ============================================================
    private View buildKeypad() {
        LinearLayout keypad = new LinearLayout(this);
        keypad.setOrientation(LinearLayout.VERTICAL);
        keypad.setPadding(dp(8), dp(8), dp(8), dp(8));
        keypad.setBackgroundColor(COLOR_BG);

        // الصفوف: 6 صفوف × 5 أعمدة
        String[][] rows = {
                {"DEG", "(", ")", "⌫", "C"},
                {"sin", "cos", "tan", "√", "÷"},
                {"ln",  "log", "x²",  "xʸ", "×"},
                {"asin","acos","atan","π",  "−"},
                {"e",   "n!",  "1/x", "%",  "+"},
                {"MR",  "M+",  "M−",  "±",  "="}
        };

        for (String[] row : rows) {
            LinearLayout rl = new LinearLayout(this);
            rl.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams rp = new LinearLayout.LayoutParams(-1, 0, 1f);
            rp.topMargin = dp(4);
            keypad.addView(rl, rp);

            for (String key : row) {
                Button b = makeKey(key);
                LinearLayout.LayoutParams bp = new LinearLayout.LayoutParams(0, -1, 1f);
                bp.setMargins(dp(3), 0, dp(3), 0);
                rl.addView(b, bp);
            }
        }
        return keypad;
    }

    // ============================================================
    // إنشاء زر بمظهر مخصص
    // ============================================================
    private Button makeKey(String key) {
        Button b = new Button(this);
        b.setText(key);
        b.setTextSize(15);
        b.setAllCaps(false);
        b.setTypeface(Typeface.DEFAULT_BOLD);
        b.setPadding(0, 0, 0, 0);

        int bg, fg;
        if (isDigitOrDot(key))            { bg = COLOR_SURFACE_ALT; fg = COLOR_TEXT; }
        else if (key.equals("="))         { bg = COLOR_GREEN;       fg = Color.WHITE; }
        else if (key.equals("C"))         { bg = COLOR_RED;         fg = Color.WHITE; }
        else if (key.equals("⌫"))         { bg = COLOR_ORANGE;      fg = Color.WHITE; }
        else if (isOperator(key))         { bg = COLOR_PRIMARY;     fg = Color.WHITE; }
        else if (key.equals("DEG") || key.equals("RAD"))
                                          { bg = COLOR_PURPLE;      fg = Color.WHITE; }
        else                              { bg = COLOR_SURFACE;     fg = COLOR_PRIMARY_LT; }

        GradientDrawable d = new GradientDrawable();
        d.setCornerRadius(dp(12));
        d.setColor(bg);
        d.setStroke(dp(1), COLOR_BORDER);
        b.setBackground(d);
        b.setTextColor(fg);

        b.setOnClickListener(v -> onKey(key));
        return b;
    }

    private boolean isDigitOrDot(String k) {
        return k.matches("[0-9.]") || k.equals("±");
    }
    private boolean isOperator(String k) {
        return k.equals("+") || k.equals("−") || k.equals("×")
                || k.equals("÷") || k.equals("%") || k.equals("xʸ");
    }

    // ============================================================
    // منطق الأزرار
    // ============================================================
    private void onKey(String key) {
        switch (key) {
            case "C":
                expression = "";
                break;
            case "⌫":
                if (!expression.isEmpty())
                    expression = expression.substring(0, expression.length() - 1);
                break;
            case "DEG":
                useDegrees = !useDegrees;
                updateAngleBadge();
                break;
            case "=":
                evaluateAndSave();
                return;
            case "±":
                if (!expression.isEmpty()) {
                    if (expression.startsWith("-")) expression = expression.substring(1);
                    else expression = "-" + expression;
                }
                break;
            case "MR":
                expression += trimNum(memory);
                break;
            case "M+":
                memory += parseOrZero(expression);
                break;
            case "M−":
                memory -= parseOrZero(expression);
                break;
            case "sin": expression += "sin("; break;
            case "cos": expression += "cos("; break;
            case "tan": expression += "tan("; break;
            case "asin":expression += "asin("; break;
            case "acos":expression += "acos("; break;
            case "atan":expression += "atan("; break;
            case "√":   expression += "√("; break;
            case "ln":  expression += "ln("; break;
            case "log": expression += "log("; break;
            case "x²":  expression += "^2"; break;
            case "xʸ":  expression += "^"; break;
            case "n!":  expression += "!"; break;
            case "1/x": expression = "1/(" + expression + ")"; break;
            case "π":   expression += "π"; break;
            case "e":   expression += "e"; break;
            case "×":   expression += "×"; break;
            case "÷":   expression += "÷"; break;
            case "−":   expression += "−"; break;
            case "+":   expression += "+"; break;
            case "%":   expression += "%"; break;
            default:    expression += key; break;
        }
        updateDisplay();
    }

    private double parseOrZero(String s) {
        try { return Double.parseDouble(s); } catch (Exception e) { return 0; }
    }

    private void evaluateAndSave() {
        if (expression.isEmpty()) return;
        try {
            double result = new Evaluator(expression, useDegrees).evaluate();
            String resStr = trimNum(result);
            history.add(expression + " = " + resStr);
            if (history.size() > 50) history.remove(0);
            refreshHistory();

            expression = resStr;
            updateDisplay();
        } catch (Exception e) {
            resultView.setText("خطأ");
            resultView.setTextColor(COLOR_RED_LT);
        }
    }

    private String trimNum(double v) {
        if (Double.isNaN(v) || Double.isInfinite(v)) return "خطأ";
        if (v == Math.rint(v) && Math.abs(v) < 1e15)
            return String.valueOf((long) v);
        String s = String.valueOf(v);
        return s.length() > 14 ? String.format("%.10g", v) : s;
    }

    // ============================================================
    // تحديث الشاشة
    // ============================================================
    private void updateDisplay() {
        exprView.setText(expression.isEmpty() ? "0" : expression);
        memView.setText(memory != 0 ? "M=" + trimNum(memory) : "");

        resultView.setTextColor(COLOR_TEXT);
        if (expression.isEmpty()) {
            resultView.setText("0");
            return;
        }
        try {
            double v = new Evaluator(expression, useDegrees).evaluate();
            resultView.setText(trimNum(v));
        } catch (Exception e) {
            resultView.setText("");
        }
    }

    // ============================================================
    // Evaluator — محلل تعبيرات رياضي كامل (recursive descent)
    // ============================================================
    private static class Evaluator {

        private final String src;
        private final boolean degrees;
        private int pos = 0;

        Evaluator(String src, boolean degrees) {
            this.src = src.replace(" ", "")
                           .replace("×", "*")
                           .replace("÷", "/")
                           .replace("−", "-")
                           .replace("√", "sqrt");
            this.degrees = degrees;
        }

        double evaluate() {
            double v = parseExpr();
            if (pos < src.length())
                throw new RuntimeException("حرف غير متوقع: " + src.charAt(pos));
            return v;
        }

        // expr := term (('+'|'-') term)*
        private double parseExpr() {
            double v = parseTerm();
            while (pos < src.length() &&
                   (src.charAt(pos) == '+' || src.charAt(pos) == '-')) {
                char op = src.charAt(pos++);
                double r = parseTerm();
                v = op == '+' ? v + r : v - r;
            }
            return v;
        }

        // term := factor (('*'|'/'|'%') factor)*
        private double parseTerm() {
            double v = parseFactor();
            while (pos < src.length()) {
                char c = src.charAt(pos);
                if (c == '*' || c == '/' || c == '%') {
                    pos++;
                    double r = parseFactor();
                    if (c == '*') v *= r;
                    else if (c == '/') v /= r;
                    else v %= r;
                } else if (c == '(' || Character.isLetter(c)
                           || Character.isDigit(c) || c == '.') {
                    // ضرب ضمني مثل 2π أو 2(3+4)
                    double r = parseFactor();
                    v *= r;
                } else break;
            }
            return v;
        }

        // factor := unary ('^' factor)?
        private double parseFactor() {
            double v = parseUnary();
            if (pos < src.length() && src.charAt(pos) == '^') {
                pos++;
                double exp = parseFactor();
                v = Math.pow(v, exp);
            }
            return v;
        }

        // unary := ('-'|'+')? primary postfix*
        private double parseUnary() {
            if (pos < src.length() && src.charAt(pos) == '-') {
                pos++;
                return -parseUnary();
            }
            if (pos < src.length() && src.charAt(pos) == '+') {
                pos++;
                return parseUnary();
            }
            double v = parsePrimary();
            // postfix: ! factorial
            while (pos < src.length() && src.charAt(pos) == '!') {
                pos++;
                v = factorial(v);
            }
            return v;
        }

        private double parsePrimary() {
            if (pos >= src.length()) throw new RuntimeException("نهاية غير متوقعة");

            char c = src.charAt(pos);

            // قوس
            if (c == '(') {
                pos++;
                double v = parseExpr();
                if (pos >= src.length() || src.charAt(pos) != ')')
                    throw new RuntimeException("قوس مفقود");
                pos++;
                return v;
            }

            // أرقام
            if (Character.isDigit(c) || c == '.') {
                int start = pos;
                while (pos < src.length() &&
                       (Character.isDigit(src.charAt(pos)) || src.charAt(pos) == '.'))
                    pos++;
                return Double.parseDouble(src.substring(start, pos));
            }

            // دوال وكلمات
            if (Character.isLetter(c)) {
                int start = pos;
                while (pos < src.length() && Character.isLetter(src.charAt(pos)))
                    pos++;
                String name = src.substring(start, pos);

                switch (name) {
                    case "pi": case "π": return Math.PI;
                    case "e":  return Math.E;
                    case "sqrt": return Math.sqrt(parseArg());
                    case "sin":  return Math.sin(toRad(parseArg()));
                    case "cos":  return Math.cos(toRad(parseArg()));
                    case "tan":  return Math.tan(toRad(parseArg()));
                    case "asin": return fromRad(Math.asin(parseArg()));
                    case "acos": return fromRad(Math.acos(parseArg()));
                    case "atan": return fromRad(Math.atan(parseArg()));
                    case "ln":   return Math.log(parseArg());
                    case "log":  return Math.log10(parseArg());
                    default:
                        throw new RuntimeException("دالة غير معروفة: " + name);
                }
            }

            // باي مباشرة
            if (c == 'π') { pos++; return Math.PI; }

            throw new RuntimeException("حرف غير متوقع: " + c);
        }

        private double parseArg() {
            // يدعم sin30 أو sin(30)
            if (pos < src.length() && src.charAt(pos) == '(') {
                pos++;
                double v = parseExpr();
                if (pos < src.length() && src.charAt(pos) == ')') pos++;
                return v;
            }
            // بدون قوس: خُذ primary واحد
            return parsePrimary();
        }

        private double toRad(double v)   { return degrees ? Math.toRadians(v) : v; }
        private double fromRad(double v) { return degrees ? Math.toDegrees(v) : v; }

        private double factorial(double n) {
            if (n < 0 || n != Math.rint(n)) throw new RuntimeException("مضروب غير صحيح");
            if (n > 170) return Double.POSITIVE_INFINITY;
            double r = 1;
            for (int i = 2; i <= (int) n; i++) r *= i;
            return r;
        }
    }
}