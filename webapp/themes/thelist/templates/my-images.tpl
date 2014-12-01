{include file="header.tpl" }



    <div class="container">
      <div class="row">

<h1>Welcome, {$handle}</h1>

{if $list}
{foreach from=$list item=foo}
    <h3>{$foo.title}</h3>

    <p><a href="{$foo.url}" /><img src="{$foo.url}" width="200" /></a></p>

{/foreach}

{/if}

</div>
</div>

{include file="footer.tpl" }
