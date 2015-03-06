{include file="header.tpl" }



    <div class="container">
      <div class="row">
      <div class="col-md-12">

<div class="bg-warning">Please note, there is a bug where the first item you add to your list may not appear. We're aware of this issue.</div>

<h1>My List</h1>

{if $msg}<div class="bg-info"><h6 style="padding: 1em" class="text-center">{$msg}</h6></div>{/if}

{if $list}
<ul>
{foreach from=$list item=foo}
    <li><a href="upload.php?id={$foo.id}">{$foo.title}</a></li>
{/foreach}
</ul>

<p></p><a class="btn btn-success" href="my-list.php"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span> Add to my list</a>&nbsp;
<a class="btn btn-primary" href="my-list.php"><span class="glyphicon glyphicon-random" aria-hidden="true"></span> Shuffle approved lists</a></p>


{else}

<p>It looks like your list is empty.<br /> Add subjects to your list and start adding to the public commons.</p>

{/if}

{if $newlist}

<h2>Add to your list from approved lists</h2>

{foreach from=$newlist item=foo}
<form action="./add-to-my-list.php" method="post">
    <input type="hidden" name="list-item" value="{$foo.id}" />
    <button type="submit" class="btn btn-default"><span class="glyphicon glyphicon-ok" aria-hidden="true"></span> {$foo.title}</button>
</form>
{/foreach}

{/if}



</div>
</div>
</div>

{include file="footer.tpl" }
