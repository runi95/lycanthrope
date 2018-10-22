<#import "master.ftlh" as m>
<@m.master>
    <#setting number_format="computer">
<div style="margin-top: 32px;">
    <div style="display: flex; flex-wrap: wrap;">
        <div style="width: ${(112 * lobby.board.dimX)}px;">
            <ul id="board" class="style-none" style="width: ${(112 * lobby.board.dimX)}px; padding: 0;">
                <#if lobby ??>
                    <#list (lobby.board.dimY - 1)..0 as y>
                        <li>
                            <div class="d-flex<#if y % 2 == 0> flex-row-reverse</#if>">
                                <#list (lobby.board.dimX - 1)..0 as x>
                                    <#assign square = lobby.board.squares[y * lobby.board.dimX + x]>
                                    <div id="${square.number}"
                                         class="square<#if square.isWormhole()> wormhole</#if><#if square.number == lobby.board.start> text-info</#if><#if square.number == lobby.board.goal> text-warning</#if>">
                                        <div class="player-container">
                                            <div id="p${square.number}" class="d-flex flex-wrap">
                                                <#list lobby.users as user>
                                                    <#if user.squareNumber == square.number>
                                                        <div id="u${user.playerNumber}"
                                                             class="player player-${user.playerNumber}"></div>
                                                    </#if>
                                                </#list>
                                            </div>
                                        </div>
                                        <div class="square-content">
                                            <#if square.isWormhole()>
                                                ${square.getWormhole()}
                                            <#else>
                                                ${square.number}
                                            </#if>
                                        </div>
                                    </div>
                                </#list>
                            </div>
                        </li>
                    </#list>
                <#else>
                    Something went wrong, please try again.
                </#if>
            </ul>
        </div>
        <div style="margin: 16px;">
            <h4>Players:</h4>
            <#list lobby.users as user>
                <div>
                    <div id="ul${user.playerNumber}" class="player-text-${user.playerNumber}">${user.nickname}</div>
                </div>
            </#list>
            <hr>
            <div>
                <div style="display: block; margin-left: auto; margin-right: auto; width: 64px;">
                    <img id="diceimg" src="/img/Dice_1.png" height="64" width="64">
                </div>
                <div>
                    <button id="diceButton" class="btn btn-primary <#if lobby.currentPlayerId != user.playerNumber>disabled</#if>" <#if lobby.currentPlayerId != user.playerNumber>disabled</#if> onclick="rollDiceAction()">Roll the
                        dice
                    </button>
                </div>
            </div>
        </div>
    </div>
</div>
</@m.master>