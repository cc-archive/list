{include file="header.tpl" }



    <div class="container">
      <div class="row">

<h1>Welcome, {$handle}</h1>

<div class="col-md-5">

{if $list}
you have a list

<ul>
{foreach from=$list item=foo}
    <li>{$foo.title} <a href="del.php?id={$foo.id}&maker={$foo.makerid}"><span class="glyphicon glyphicon-trash">&nbsp;</span></a></li>
{/foreach}
</ul>


{else}

<p>It looks like your list is empty.</p>

{/if}

</div>

<div class="col-md-6 well">

<h3>Add subjects to your list and start adding to the public commons.</h3>

<form role="form" action="add-save.php" method="post">

  <div class="form-group">
    <label for="list-title">Subject</label>
    <input type="text" class="form-control" id="list-title" name="list-title" placeholder="Morrissey" required>
  </div>
  <div class="form-group">
    <label for="list-url">URL to subject</label>
    <input type="url" class="form-control" name="list-url" id="list-url" placeholder="http://en.wikipedia.org/wiki/Morrissey">
  </div>
  <div class="form-group">
    <label for="list-description">Optional description</label>
    <p><textarea class="form-control" name="list-description" id="list-description"></textarea></p>
    <p class="help-block">Example block-level help text here.</p>
  </div>
  <button type="submit" class="btn btn-success">Submit</button>

</form>


</div>
</div>
</div>
</div>

{include file="footer.tpl" }
