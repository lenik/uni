<script setup lang="ts">

import from "font-awesome/css/font-awesome.css";

import { ref, onMounted } from 'vue';
import moment from 'moment';
import clndr from 'clndr';

const props = defineProps<{
    date: string
}>();

const template = `
    <table class='clndr-table' border='0' cellspacing='0' cellpadding='0'>
        <caption>
            <div class='nav flex-row with-gap'>
                <i class='fa fa-angle-double-left clndr-previous-year-button'></i>
                <i class='fa fa-angle-left clndr-previous-button'></i>
                <div class='month fill'>
                    <%= year %> 年
                    <%= month %>
                    (<%= days[0].date.toLunar().aYear %>年
                    <%= days[0].date.toLunar().monthName %>月)
                </div>
                <i class='fa fa-angle-right clndr-next-button'></i>
                <i class='fa fa-angle-double-right clndr-next-year-button'></i>
            </div>
        </caption>
        <thead>
            <tr class='header-days'>
            <% for (var i = 0; i < daysOfTheWeek.length; i++) { %>
                <td class='header-day'><%= daysOfTheWeek[i] %></td>
            <% } %>
            </tr>
        </thead>
        <tbody>
        <% for (var i = 0; i < numberOfRows; i++) { %>
            <tr>
            <% for (var j = 0; j < 7; j++) { %>
                <% var d = j + i * 7; %>
                <td class='<%= days[d].classes %>'>
                    <div class="flex-column">
                        <div class="solar fill"><%= days[d].day %></div>
                        <div class="lunar">
                            <%= days[d].date.toLunar().dayName %>
                        </div>
                    </div>
                </td>
            <% } %>
            </tr>
        <% } %>
        </tbody>
    </table>`;

const div = ref(null);
moment.locale("zh-cn");

onMounted(() => {
    $(div).clndr({
        template: template,
        clickEvents: {
            click: function (target) {
                console.log('Cal-2 clicked: ', target);
            },
            nextInterval: function () {
                console.log('Cal-2 next interval');
            },
            previousInterval: function () {
                console.log('Cal-2 previous interval');
            },
            onIntervalChange: function () {
                console.log('Cal-2 interval changed');
            }
        }
    });
});
</script>

<template>
    <div ref="div" class="dark"></div>
</template>

<style scoped lang="scss">
.dark .clndr {
    .nav.fa:hover {
        background: #789;
    }

    table {
        border-color: #555;
    }

    thead {
        background-color: #336688;
    }

    tbody {
        color: #fff;

        tr {
            background: #3a3f4e;

            &:nth-child(even) {
                background: #333344;
            }
        }
    }

    .day {
        border-color: #555;

        .lunar {
            color: #9ab;
        }

        &:hover {
            background: #556;
        }
    }

    .today {
        background-color: #667799;
    }

    .adjacent-month {
        background-color: #666a77;
        color: #aaa;
    }
}

.clndr {
    font-family: sans;

    .nav {
        .fa {
            width: 1.2em;
            height: 1.2em;
            margin: 3px;
            border-radius: 100px;

            &:hover {
                cursor: pointer;
            }
        }
    }

    table {
        border-style: solid;
        border-width: 1px;
        border-collapse: collapse;
        color: inherit;
    }

    thead {
        font-weight: bold;
        text-align: center;

        td {
            padding: 0.5em;
        }
    }

    .day {
        width: 4em;
        height: 4em;
        border-style: solid;
        border-width: 1px;
        border-collapse: collapse;
        box-sizing: border-box;
        cursor: pointer;

        .flex-column {
            height: 100%;
            padding: .3em;
        }

        .solar {
            text-align: right;
        }

        .lunar {
            font-size: 80%;
        }
    }

    .today {
        font-weight: bold;
    }

    .adjacent-month {
        font-style: italic;
        cursor: default;
    }
}</style>