class ComplexExpression {
    public static ComplexNumber evaluate(String[] args) {
        ComplexNumber result = parseComplex(args[0]);

        for (int i = 1; i < args.length; i += 2) {
            String operator = args[i];
            ComplexNumber nextComplex = parseComplex(args[i + 1]);

            switch (operator) {
                case "+":
                    result = result.add(nextComplex);
                    break;
                case "-":
                    result = result.subtract(nextComplex);
                    break;
                case "*":
                    result = result.multiply(nextComplex);
                    break;
                case "/":
                    result = result.divide(nextComplex);
                    break;
                default:
                    throw new IllegalArgumentException("Operator necunoscut: " + operator);
            }
        }
        return result;
    }

    private static ComplexNumber parseComplex(String str) {
        str = str.replace("i", "");
        String[] parts = str.split("\\+|(?=-)"); // sablon regex (gasit)

        double real = Double.parseDouble(parts[0].trim());
        double imaginary = Double.parseDouble(parts[1].trim());

        return new ComplexNumber(real, imaginary);
    }
}