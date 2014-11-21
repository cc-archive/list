{include file="header.tpl" }

<div class="container">

<div class="row">

<div class="col-md-12">

<h1>Upload an image</h1>

<form action="upload-image.php" method="post" enctype="multipart/form-data">
  <div class="form-group">
    <input type="file" name="list-image">
    <input type="hidden" name="id" value="{$id}" />
  </div>
  <input class="btn btn-success" name="upload" type="submit" value="Upload" />
</form>


</div>
</div>
</div>




{include file="footer.tpl" }
