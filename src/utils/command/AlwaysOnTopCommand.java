package utils.command;

public class AlwaysOnTopCommand implements Command {
    @Override
    public boolean matches(String input) {
        return input.equalsIgnoreCase("/hide") || input.equalsIgnoreCase("/show");
    }

    @Override
    public void execute(String input, CommandContext context) {
        System.out.println("Hello");
        boolean show = true;

        if(show = input.equalsIgnoreCase("/show")){
            System.out.println("Hello");
            context.frame.setAlwaysOnTop(true);
            context.inputField.setText("");

        }else if (input.equalsIgnoreCase("/hide")){
            System.out.println("Hello");
            context.frame.setAlwaysOnTop(false);
            context.inputField.setText("");

        }


    }
}