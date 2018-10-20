<div class="container">
    <div class="row">
        <div class="col-8 offset-2">
            <div class="text-center">
                <#if board??>
                    <h1>
                        You are now in ${board.name}
                    </h1>
                    <div id="lobbytable" class="d-flex flex-column">

                    </div>
                <#else>
                    <div class="text-danger">
                        Something went wrong!
                    </div>
                </#if>
            </div>
        </div>
    </div>
</div>