{include file="header.tpl" }



    <div class="container">
      <div class="row">

<h1>My List</h1>

{if $list}
you have a list

<ul>
{foreach from=$list item=foo}
    <li>{$foo}</li>
{/foreach}
</ul>


{else}

<p>It looks like your list is empty.<br /> Add subjects to your list and start adding to the public commons.</p>

<p><a class="btn btn-success" href="/my-list.php">Add to my list</a></p>

{/if}


</div>
</div>

{include file="footer.tpl" }
