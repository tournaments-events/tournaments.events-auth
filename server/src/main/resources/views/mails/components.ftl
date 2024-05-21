<!--
Library of components to write mail using the StackOverflow template.
-->

<!--
A single column
-->
<#macro single_column>
    <tr>
        <td style="padding: 30px; background-color: #ffffff;" class="sm-p bar">
            <table border="0" cellpadding="0" cellspacing="0" role="presentation" style="width:100%;">
                <#nested>
            </table>
        </td>
    </tr>
</#macro>

<!--
Rich text
-->
<#macro rich_text>
    <tr>
        <td style="padding-bottom: 15px; font-family: arial, sans-serif; font-size: 15px; line-height: 21px; color: #3C3F44; text-align: left;">
        </td>
    </tr>
</#macro>
