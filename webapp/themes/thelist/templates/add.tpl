{include file="header.tpl" }



    <div class="container">
      <div class="row">

<h1>Welcome, {$handle}</h1>

<div class="well">

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
    <p class="help-block"></p>
    <label for="list-category">Category</label>
    <p><select name="list-category" id="list-category">
      {foreach from=$categories item=cat}
	<option vale="{$cat.id}">{$cat.title}</option>
      {/foreach}
    </select></p>
  </div>
  <button type="submit" class="btn btn-success">Submit</button>

</form>


</div>


<div class="col-md-5">

{if $list}
you have a list

<ul>
{foreach from=$list item=foo}
    <li>{$foo.title} <a href="del.php?id={$foo.id}&maker={$foo.makerid}"><span class="glyphicon glyphicon-trash">&nbsp;</span></a></li>
{/foreach}
</ul>



{/if}

</div>

</div>
</div>
</div>

{include file="footer.tpl" }
