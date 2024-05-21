<#import "/mails/template.ftl" as t>
<#import "/mails/components.ftl" as c>

<!DOCTYPE html>
<html lang="en"
      xmlns="http://www.w3.org/1999/xhtml"
>

<@t.head></@t.head>

<@t.body>
    <@c.single_column>
        <@c.rich_text>
            Your validation code is ${code}
        </@c.rich_text>
    </@c.single_column>
</@t.body>

</html>
