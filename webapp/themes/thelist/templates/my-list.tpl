{include file="header.tpl" }



    <div class="container">
      <div class="row">
      <div class="col-md-12">

<h1>My List</h1>

{if $msg}<div class="bg-info"><h6 style="padding: 1em" class="text-center">{$msg}</h6></div>{/if}

{if $list}
<ul>
{foreach from=$list item=foo}
    <li><a href="/upload.php?id={$foo.id}">{$foo.title}</a></li>
{/foreach}
</ul>

<a class="btn btn-info" href="/my-list.php">Add to my list</a>

{else}

<p>It looks like your list is empty.<br /> Add subjects to your list and start adding to the public commons.</p>

{/if}

{if $newlist}

<h2>Add to your list from approved lists</h2>

{foreach from=$newlist item=foo}
<form action="add-to-my-list.php" method="post">
    <input type="hidden" name="list-item" value="{$foo.id}" />
    <button type="submit" class="btn btn-success">{$foo.title}</button>
</form>
{/foreach}

{/if}



</div>
</div>
</div>

{include file="footer.tpl" }
